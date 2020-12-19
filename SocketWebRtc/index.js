'use strict';

var log4js = require('log4js');
var loggerConfig = require('./config/logger.js');
loggerConfig.config(log4js);
var logger = log4js.getLogger('WebRTC');

const { convertJson } = require('./utils/index');

var os = require('os');
var nodeStatic = require('node-static');
var http = require('http');
var socketIO = require('socket.io');

logger.info("  ");
logger.info("-----> WebRTC SERVICE START <-----");
logger.info("  ");

var fileServer = new(nodeStatic.Server)();
var app = http.createServer(function(req, res) {
  fileServer.serve(req, res);
}).listen(6001);

const arrUserInfo = [];

var io = socketIO.listen(app);
io.sockets.on('connection', function(socket) {
  socket.on('message', function(message) {
    logger.info('Client said: ', message);
    socket.broadcast.emit('message', message);
  });

  // create or join room
  socket.on('create_or_join', function(room) {
    logger.info('AI_DO_KET_NOI: ' + socket.id);
    io.emit('AI_DO_KET_NOI', socket.id);

    // user online
    arrUserInfo.push(socket.id);
    io.emit('DANH_SACH_ONLINE', arrUserInfo);

    logger.info('Received request to create or join room ' + room);
    var clientsInRoom = io.sockets.adapter.rooms[room];
    var numClients = clientsInRoom ? Object.keys(clientsInRoom.sockets).length : 0;
    logger.info('Room ' + room + ' now has ' + numClients + ' client(s)');
    if (numClients === 0) {
      socket.join(room);
      logger.info('Client ID ' + socket.id + ' created room ' + room);
      socket.emit('created', room, socket.id);
    } else {
      logger.info('Client ID ' + socket.id + ' joined room ' + room);
      io.sockets.in(room).emit('join', room);
      socket.join(room);
      socket.emit('joined', room, socket.id);
      io.sockets.in(room).emit('ready');
    }
  });

  // video call moi
	socket.on('NEW_VIDEO_CALL', data => {
		logger.info('NEW_VIDEO_CALL', JSON.stringify(data));

    data = convertJson(data);
    
		if (data && Object.keys(data).includes('caller_id')) {
      logger.info('NEW_VIDEO_CALL caller', data.caller_id);
			socket.broadcast.to(data.caller_id).emit('NEW_VIDEO_CALL', data);
    }
    if (data && Object.keys(data).includes('callee_id')) {
      logger.info('NEW_VIDEO_CALL callee', data.callee_id);
			socket.broadcast.to(data.callee_id).emit('NEW_VIDEO_CALL', data);
		}
  });
  
  // huy video call
	socket.on('CANCEL_VIDEO_CALL', data => {
    logger.info('CANCEL_VIDEO_CALL', JSON.stringify(data));

    data = convertJson(data);
    
    if (data && Object.keys(data).includes('caller_id')) {
      logger.info('CANCEL_VIDEO_CALL caller', data.caller_id);
			socket.broadcast.to(data.caller_id).emit('CANCEL_VIDEO_CALL', data);
    }
    if (data && Object.keys(data).includes('callee_id')) {
      logger.info('CANCEL_VIDEO_CALL callee', data.callee_id);
			socket.broadcast.to(data.callee_id).emit('CANCEL_VIDEO_CALL', data);
		}
  });
  
  // dong y video call
	socket.on('CONFIRM_VIDEO_CALL', data => {
    logger.info('CONFIRM_VIDEO_CALL', JSON.stringify(data));

    data = convertJson(data);
    
    if (data && Object.keys(data).includes('caller_id')) {
      logger.info('CONFIRM_VIDEO_CALL caller', data.caller_id);
			socket.broadcast.to(data.caller_id).emit('CONFIRM_VIDEO_CALL', data);
    }
    if (data && Object.keys(data).includes('callee_id')) {
      logger.info('CONFIRM_VIDEO_CALL callee', data.callee_id);
			socket.broadcast.to(data.callee_id).emit('CONFIRM_VIDEO_CALL', data);
		}
  });
  
  // ket thuc video call
	socket.on('END_VIDEO_CALL', data => {
    logger.info('END_VIDEO_CALL', JSON.stringify(data));

    data = convertJson(data);
    
    if (data && Object.keys(data).includes('caller_id')) {
      logger.info('END_VIDEO_CALL caller', data.caller_id);
			socket.broadcast.to(data.caller_id).emit('END_VIDEO_CALL', data);
    }
    if (data && Object.keys(data).includes('callee_id')) {
      logger.info('END_VIDEO_CALL callee', data.callee_id);
			socket.broadcast.to(data.callee_id).emit('END_VIDEO_CALL', data);
		}
	});

  // chat moi
	socket.on('NEW_CHAT', data => {
		logger.info('NEW_CHAT', JSON.stringify(data));

    data = convertJson(data);
    
		if (data && Object.keys(data).includes('caller_id')) {
      logger.info('NEW_CHAT caller', data.caller_id);
			socket.broadcast.to(data.caller_id).emit('NEW_CHAT', data);
    }
    if (data && Object.keys(data).includes('callee_id')) {
      logger.info('NEW_CHAT callee', data.callee_id);
			socket.broadcast.to(data.callee_id).emit('NEW_CHAT', data);
		}
  });

  // ket thuc chat
	socket.on('END_CHAT', data => {
		logger.info('END_CHAT', JSON.stringify(data));

    data = convertJson(data);
    
		if (data && Object.keys(data).includes('caller_id')) {
      logger.info('END_CHAT caller', data.caller_id);
			socket.broadcast.to(data.caller_id).emit('END_CHAT', data);
    }
    if (data && Object.keys(data).includes('callee_id')) {
      logger.info('END_CHAT callee', data.callee_id);
			socket.broadcast.to(data.callee_id).emit('END_CHAT', data);
		}
  });

  socket.on('disconnect', () => {
    logger.info('AI_DO_NGAT_KET_NOI: ' + socket.id);
    const index = arrUserInfo.indexOf(socket.id);
    if (index != -1) {
      arrUserInfo.splice(index, 1);
      logger.info('disconnect DANH_SACH_ONLINE: ' + JSON.stringify(arrUserInfo));
  
      io.emit('AI_DO_NGAT_KET_NOI', socket.id);
    }
  });

  socket.on('ipaddr', function() {
    var ifaces = os.networkInterfaces();
    for (var dev in ifaces) {
      ifaces[dev].forEach(function(details) {
        if (details.family === 'IPv4' && details.address !== '127.0.0.1') {
          socket.emit('ipaddr', details.address);
        }
      });
    }
  });

  socket.on('bye', function() {
    logger.info('received bye');
  });

});
