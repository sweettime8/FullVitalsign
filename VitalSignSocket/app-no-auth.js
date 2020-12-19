'use strict';

let express = require('express');
let fs = require('fs');
let app = express();
let bodyParser = require('body-parser');
const chalk = require('chalk');
const _cfg = require('./_cfg/_cfg');
const log4js = require('log4js');

log4js.configure({
  /* trace --> debug --> info --> warn --> error --> fatal */
  appenders: { logsInterview: { type: 'file', filename: 'interview-logs.out' } },
  //appenders: { logsInterview: { type: 'console' } },
  categories: { default: { appenders: ['logsInterview'], level: 'info' } }
});
const logger = log4js.getLogger('logsInterview');

//let server = require('http').createServer(app);
let server = require('https').createServer({key: fs.readFileSync(_cfg.TLS_PRIVKEY), cert: fs.readFileSync(_cfg.TLS_FULLCHAIN)}, app);

let io = require('socket.io').listen(server);
let port = process.env.PORT || _cfg.SERVER_PORT;
server.listen(port, () => {
  logger.info('Server PORT %d', port);
});

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.get('/', function (req, res) {
  res.send('Interview socket');
});
app.get('/favicon.ico', (req, res) => res.status(204));
app.use(function (err, req, res, next) {
  let errorObj = {
    errorMessage: err.message
  };
  res.status(500).send(errorObj);
});

/* SOCKET */
let mapsEmailWithSocketId = {}; //Maps các email kèm socketId đã kết nối
//let mapsRoomName = {};          //Maps các email kèm roomName đang join
io.sockets.on('connection', function (socket) {
  
  if( socket.handshake.query && socket.handshake.query.email ) {
    let email = socket.handshake.query.email;
    socket.email = email;
    if( !mapsEmailWithSocketId[email] )
      mapsEmailWithSocketId[email] = [socket.id];
    else
      mapsEmailWithSocketId[email].push(socket.id);
    updateUsers();

    //Nếu vẫn đang nằm trong map room (Vừa connect/reConnect nhưng room cũ gần nhất chưa leave)
    /*let roomNameInSet = mapsRoomName[email];
    if( roomNameInSet ) {
      socket.join(roomNameInSet);
      socket.roomName = roomNameInSet;
      logger.info(email + ' reConnect, reJoind room[' + roomNameInSet + ']');
      updateUsersInRoom();
    }*/

    logger.info(socket.id + ' connected, '+ email + ' is online!' + chalk.green('✓'));
  }

  socket.on('joinRoom', function (dataReceive) {
    if( dataReceive && dataReceive.roomName ) {
      socket.roomName = dataReceive.roomName;
      socket.join(socket.roomName);
      //mapsRoomName[socket.email] = socket.roomName;
      logger.info('['+socket.id + '] ['+socket.email+'] joined roomn [' + socket.roomName + ']');
      updateUsersInRoom();
    }
  });

  socket.on('leaveRoom', function (payLoad) {
    if( socket.roomName && payLoad && payLoad.roomName ) {
      socket.leave(payLoad.roomName);
      
      //if( mapsRoomName[socket.email] )
        //delete mapsRoomName[socket.email];

      logger.info(socket.email + ' leave room [' + payLoad.roomName + ']');
      updateUsersInRoom();
    }
  });

  socket.on('checkInRoom', function (dataReceive, fn) {
    let result = false;
    if( dataReceive && dataReceive.email ) {
      let socketIdTarget = mapsEmailWithSocketId[dataReceive.email];
      if( socketIdTarget && socketIdTarget.length > 0 ) {
        let objSocketInRoom = io.sockets.adapter.sids[socketIdTarget[socketIdTarget.length-1]];
        //logger.info(objSocketInRoom);
        if( objSocketInRoom && Object.keys(objSocketInRoom).length === 1 )
          result = true;
      }else
        logger.info('socketId not found!');
    }
    fn(result);
  });

  function updateUsersInRoom() {
    if( socket.roomName ) {
      let emailLst = [];
      let clients = io.sockets.adapter.rooms[socket.roomName];
      if( clients ) {
        for (let clientId in clients.sockets ) {
            let socketClient = io.sockets.connected[clientId];
            emailLst.push(socketClient.email);
        }
        io.in(socket.roomName).emit('userOnlineInRoom', emailLst);
      }
      else
        logger.error('Room[' + socket.roomName + '] is invalid');
    }
    else
      logger.info('socket.roomName is null');
  }

  function updateUsers() {
    /*setTimeout(() => {
      logger.error('------------------------------');
      logger.error(mapsEmailWithSocketId);
      logger.error('------------------------------');
      io.sockets.emit('userOnline', mapsEmailWithSocketId);  
    }, 1000);*/
    io.sockets.emit('userOnline', mapsEmailWithSocketId);
  }

  function isAliveSocket(socketId) {
    return io.sockets.sockets[socketId] !== undefined;
  }

  socket.on('clientSend', function (payLoad) {
    payLoad.socketIdSend = socket.id;

    // Chỉ gửi cho 1 socketId mong muốn, flow mời call video
    if( payLoad.socketIdTarget && isAliveSocket(payLoad.socketIdTarget) )
      io.sockets.connected[payLoad.socketIdTarget].emit('clientReceive', payLoad);
    else {
      if( payLoad && payLoad.emailReceive ) {
        let socketIdTarget = mapsEmailWithSocketId[payLoad.emailReceive];
        if( socketIdTarget && socketIdTarget.length > 0 ) {
          try {
            io.sockets.connected[socketIdTarget[socketIdTarget.length - 1]].emit('clientReceive', payLoad);
            logger.info(payLoad);
            logger.info('Send succes to socketId [' + socketIdTarget[socketIdTarget.length - 1] + '] ' + chalk.green('✓'));      
          }catch(e) {
            logger.error('Send fail to socketId [' + socketIdTarget[socketIdTarget.length - 1] + '] ' + chalk.red('X'));
          }
        }else
          logger.error('socketIdTarget is null ' + chalk.red('X'));
      }
    }
  });

  socket.on('disconnect', function () {
    if( mapsEmailWithSocketId && socket.email ) {
      if( mapsEmailWithSocketId[socket.email] && mapsEmailWithSocketId[socket.email].length > 0 ) {
        if( mapsEmailWithSocketId[socket.email].length === 1 ) {
          delete mapsEmailWithSocketId[socket.email];
          logger.info('keyMaps removed, socketId: ' + socket.id +  ', ' + socket.email  + ' is offline!' + chalk.red('X'));
        }else {
          mapsEmailWithSocketId[socket.email].splice(mapsEmailWithSocketId[socket.email].indexOf(socket.id), 1);
          logger.info('socketId removed, socketId: ' + socket.id + ' ' + chalk.green('✓'));                   
        }
        io.sockets.emit('userOffline', socket.email);

        /*if( socket.roomName && mapsRoomName[socket.email] ) {
          delete mapsRoomName[socket.email];
          logger.info(socket.email + ' leave room [' + socket.roomName + ']');
        }*/

        updateUsers();
        updateUsersInRoom();
      }
    }
  });

});