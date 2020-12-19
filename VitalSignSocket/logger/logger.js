'use strict';

const log4js = require('log4js');

log4js.configure({
  appenders: {
    out:{ type: 'console' },
    //app:{ type: 'file', filename: 'logs/co-learn-logs.out' }
    app:{ type: 'dateFile', daysToKeep: 30, filename: 'logs/vs-logs.out' }
  },
  categories: {
    default: { appenders: [ 'out', 'app' ], level: 'info' }
  }
});

module.exports = {
    instance: log4js.getLogger()
}