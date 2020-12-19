'use strict';

module.exports = {
	convertJson(data) {
		if (typeof data === 'string') {
			const ojb = {};
			try {
				JSON.parse(data);
			} catch (e) {
				return ojb;
			}

			return JSON.parse(data);
		}

	  	return data;
	}
}