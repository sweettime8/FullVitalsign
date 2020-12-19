'use strict';

let express = require('express');
let router = express.Router();
let fs = require('fs');
let app = express();
let bodyParser = require('body-parser');
const _cfg = require('./_cfg/_cfg');
//const Utils = require('./utils/Utils');
//let mysql = require('mysql');
var mqtt = require('mqtt');

const logger = require('./logger/logger');

let server = require('http').createServer(app);
//let server = require('https').createServer({key: fs.readFileSync(_cfg.TLS_PRIVKEY), cert: fs.readFileSync(_cfg.TLS_FULLCHAIN)}, app);

const port = process.argv[2] || _cfg.SERVER_PORT;
server.listen(port, () => {
  logger.instance.info('Server PORT %d', port);
});

const io = require('socket.io').listen(server);

// app.use(bodyParser.urlencoded({ extended: false }));
// app.use(bodyParser.json());

//app.use('/', require('./routes/auth'));
router.get('/', function (req, res) {
  logger.instance.info('[%d] Go....', port);
  res.send('Vitalsign Socket System');
});
app.use(router);

//Đảm bảo có lỗi runTime thì ko bị crash app
process.on('uncaughtException', function (err) {
  logger.instance.error(err);
});

//var mqttClient  = mqtt.connect(_cfg.MQTT_PROTOCOL + '://' + _cfg.MQTT_HOST + ':' + _cfg.MQTT_PORT);
 
  let caFile = fs.readFileSync(_cfg.MQTT_TLS_CA_CERT);
  let certFile = fs.readFileSync(_cfg.MQTT_TLS_CLIENT_CERT);
  let keyFile = fs.readFileSync(_cfg.MQTT_TLS_CLIENT_KEY);
  let mqttConnOpt = {
    rejectUnauthorized: false,
    username: "broker-01",
    password: "Elcom@123",
    connectTimeout: 5000,
    keepalive: 1800,
    clean: false,
    clientId: '',
    ca: [ caFile ],
    cert: certFile,
    key: keyFile
  };
  //let mqttConnObject = mqtt.connect(_cfg.MQTT_PROTOCOL + '://' + _cfg.MQTT_HOST + ':' + _cfg.MQTT_PORT, mqttConnOpt);

/* SOCKET */
io.sockets.on('connection', function (socket) {
	
  //Nếu ở web là màn hình cần theo dõi data mới xử lý
  if( socket.handshake.query && socket.handshake.query.requestMonitor && socket.handshake.query.requestMonitor === 'true' ) {
    
    mqttConnOpt.clientId = 'vs-socket-cli-' + socket.id;
    socket.mqttClient = mqtt.connect(_cfg.MQTT_PROTOCOL + '://' + _cfg.MQTT_HOST + ':' + _cfg.MQTT_PORT, mqttConnOpt, function(e) {
      logger.instance.error(e);
    });
    
    //Khởi tạo mảng gateId mà web cần theo dõi
    socket.gateIds = [];

    let Queue = require('queue-fifo');
    // let queueBp = {};
    let queueSpo2 = {};
    let queueTemp = {};

    socket.mqttClient.on('message', function (topic, message) {

      if( socket.gateIds && socket.gateIds.length > 0 ) {

        socket.gateIds.forEach(gate => {

          switch (topic) {
  
              /* BP trả thẳng, không set queue */
              case _cfg.MQTT_TOPIC_DATA_BP + '_' + gate.gateId:
                let bp;
                try {
                  bp = JSON.parse(message.toString());
                }catch(e) {
                  logger.instance.error('BP parse json.ex: ' + e.toString());
                }

                if( bp && bp.gateId === gate.gateId ) {
                  socket.emit('mqttMsgFromServer', {
                              data: [bp],
                              dataType: 'BP',
                              spo2ChartId: gate.spo2ChartId,
                              ecgChartId: gate.ecgChartId,
                              spo2ShowId: gate.spo2ShowId,
                              nbpShowId: gate.nbpShowId,
                              hrShowId: gate.hrShowId
                            });
                }
              break;
  
              case _cfg.MQTT_TOPIC_DATA_SPO2 + '_' + gate.gateId:
                let spo2;
                try {
                    spo2 = JSON.parse(message.toString());
                }catch(e) {
                  logger.instance.error('SPO2 parse json.ex: ' + e.toString());
                }

                if( spo2 && spo2.gateId === gate.gateId ) {
                  queueSpo2[gate.gateId].enqueue(spo2);
                  if( queueSpo2[gate.gateId].size() === _cfg.QUEUE_MAX_ITEMS ) {
                    let dataLst = [];
                    dataLst.push(queueSpo2[gate.gateId].dequeue());
                    dataLst.push(queueSpo2[gate.gateId].dequeue());
                    dataLst.push(queueSpo2[gate.gateId].dequeue());
                    socket.emit('mqttMsgFromServer', {
                                  data: dataLst,
                                  dataType: 'SPO2',
                                  spo2ShowId: gate.spo2ShowId,
                                  nbpShowId: gate.nbpShowId,
                                  hrShowId: gate.hrShowId
                                });

                    if( socket.gateIdDetail === gate.gateId ) {
                      socket.emit('mqttMsgFromServerForDetails', {
                        data: dataLst,
                        dataType: 'SPO2'
                      });
                    }
                    //logger.instance.info('From topic [%s], for gate [%s], for socket [%s]', topic, gate.gateId, socket.id);
                  }
                }
              break;

              case _cfg.MQTT_TOPIC_DATA_TEMP + '_' + gate.gateId:
                let temp;
                try {
                  temp = JSON.parse(message.toString());
                }catch(e) {
                  logger.instance.error('TEMP parse json.ex: ' + e.toString());
                }

                if( temp && temp.gateId === gate.gateId ) {
                  queueTemp[gate.gateId].enqueue(temp);
                  if( queueTemp[gate.gateId].size() === _cfg.QUEUE_MAX_ITEMS ) {
                    let dataLst = [];
                    dataLst.push(queueTemp[gate.gateId].dequeue());
                    dataLst.push(queueTemp[gate.gateId].dequeue());
                    dataLst.push(queueTemp[gate.gateId].dequeue());
                    socket.emit('mqttMsgFromServer', {
                                  data: dataLst,
                                  dataType: 'TEMP',
                                });
                    
                    //logger.instance.info('From topic [%s], for gate [%s], for socket [%s]', topic, gate.gateId, socket.id);
                  }
                }
              break;

              default:
                break; 
          }
        });
      }
    });

    socket.on("subscribeTopic", (data) => {

      if( data && data.gateId ) {

        // Đẩy gateId vào mảng gate và subscrive mqtt theo topic cần theo dõi ( với đk nếu gateId chưa có trong mảng gateIds)
        if ( socket.gateIds.findIndex(x => x.gateId === data.gateId) === -1 ) {

          logger.instance.info('Begin subscribe data for gateId: [%s]', data.gateId);

          socket.gateIds.push({
                          gateId: data.gateId,
                          spo2ChartId: data.spo2ChartId,
                          ecgChartId: data.ecgChartId,
                          //temp01ChartId: data.temp01ChartId,
                          //temp02ChartId: data.temp02ChartId,
                          spo2ShowId: data.spo2ShowId,
                          nbpShowId: data.nbpShowId,
                          hrShowId: data.hrShowId,
                          canvas01: data.canvas01,
                          canvas02: data.canvas02
                        });
          
          if( !queueSpo2[data.gateId] )
            queueSpo2[data.gateId] = new Queue();
          // if( !queueBp[data.gateId] )
          //   queueBp[data.gateId] = new Queue();
          if( !queueTemp[data.gateId] )
            queueTemp[data.gateId] = new Queue();

          let topics = [ _cfg.MQTT_TOPIC_DATA_SPO2 + '_' + data.gateId
                      , _cfg.MQTT_TOPIC_DATA_BP + '_' + data.gateId
                      , _cfg.MQTT_TOPIC_DATA_TEMP + '_' + data.gateId ];

          topics.forEach(topic => {
            socket.mqttClient.subscribe(topic, function (err) {
              if ( !err )
                logger.instance.info('socketId [%s] SUBSCRIBE topic [%s] for gateId [%s] SUCCESS!', socket.id, topic, data.gateId);
              else
                logger.instance.info('socketId [%s] SUBSCRIBE topic [%s] for gateId [%s] FAILED!', socket.id, topic, data.gateId);
            });
          });
        }else
          logger.instance.error('gateId [%s] already SUBSCRIBE by socketId [%s]!', data.gateId, socket.id);
      }
    });

    socket.on("unSubscribeTopic", (data) => {

      if( data && data.gateIdUnSubLst ) {

        let gates = data.gateIdUnSubLst.split(',');

        gates.forEach(gateId => {

            let datas = socket.gateIds.filter(function( obj ) {
              return obj.gateId !== gateId;
            });
            socket.gateIds = datas;

            let topicsUnsubscribe = [ { 'type': _cfg.MQTT_TOPIC_DATA_SPO2, 'name' : _cfg.MQTT_TOPIC_DATA_SPO2 + '_' + gateId }
                                    , { 'type': _cfg.MQTT_TOPIC_DATA_BP, 'name' : _cfg.MQTT_TOPIC_DATA_BP + '_' + gateId }
                                    , { 'type': _cfg.MQTT_TOPIC_DATA_TEMP, 'name' : _cfg.MQTT_TOPIC_DATA_TEMP + '_' + gateId } ];
            topicsUnsubscribe.forEach(topic => {
              socket.mqttClient.unsubscribe(topic.name, function (err) {
                if ( !err ) {
                  logger.instance.info('socketId [%s] UN-SUBSCRIBE topic [%s] SUCCESS!', socket.id, topic.name);

                  // Reset queue theo gateId
                  if( topic.type === _cfg.MQTT_TOPIC_DATA_SPO2 && queueSpo2[gateId] )
                    queueSpo2[gateId] = null;
                  // else if( topic.type === _cfg.MQTT_TOPIC_DATA_BP && queueBp[gateId] )
                  //   queueBp[gateId] = null;
                  else if( topic.type === _cfg.MQTT_TOPIC_DATA_TEMP && queueTemp[gateId] )
                    queueTemp[gateId] = null;
                }
                else
                  logger.instance.info('socketId [%s] UN-SUBSCRIBE topic [%s] FAILED!', socket.id, topic.name);
              });
            });
        });
      }
    });

    socket.on('disconnect', function () {
      if( socket.gateIds && socket.gateIds.length > 0 ) {
        socket.gateIds.forEach(gate => {
          let topicsUnsubscribe = [ _cfg.MQTT_TOPIC_DATA_SPO2 + '_' + gate.gateId
                                  , _cfg.MQTT_TOPIC_DATA_BP + '_' + gate.gateId
                                  , _cfg.MQTT_TOPIC_DATA_TEMP + '_' + gate.gateId ];
          topicsUnsubscribe.forEach(topic => {
            socket.mqttClient.unsubscribe(topic, function (err) {
              if ( !err )
                logger.instance.info('socketId [%s] UN-SUBSCRIBE topic [%s] SUCCESS!', socket.id, topic);
              else
                logger.instance.info('socketId [%s] UN-SUBSCRIBE topic [%s] FAILED!', socket.id, topic);
            });
          });
        });
      }
    });
    
  }
});

/*function isAliveSocket(socketId) {
  return io.sockets.sockets[socketId] !== undefined;
}*/
