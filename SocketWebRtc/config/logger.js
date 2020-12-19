exports.config = function (log4js) {
	log4js.configure({
		appenders: {
			cheeseLogs: { type: 'file', filename: 'webrtc.log' },
			console: { type: 'console' }
		},
		categories: {
			cheese: { appenders: ['cheeseLogs'], level: 'error' },
			another: { appenders: ['console'], level: 'trace' },
			default: { appenders: ['console', 'cheeseLogs'], level: 'trace' }
		}
	});
};