'use strict';
module.exports = {
    AuthorizedUserNotFound: {
        status_code: 401,
        message: {
            text: 'Authentication failed. User not found.'
        },
        data: {},
        meta: {}
    },
    AuthorizedWrongPassword: {
        status_code: 401,
        message: {
            text: 'Authentication failed. Wrong password.'
        },
        data: {},
        meta: {}
    },
    AuthorizedTokenMissing: {
        status_code: 401,
        message: {
            text: 'Unauthorized. No token provided.'
        },
        data: {},
        meta: {}
    },
    AuthorizedTokenBad: {
        status_code: 401,
        message: {
            text: 'Unauthorized. Wrong token specfied.'
        },
        data: {},
        meta: {}
    },
    AuthorizedBadService: {
        status_code: 500,
        message: {
            text: 'Authentication failed. Server error.'
        },
        data: {},
        meta: {}
    },
    AuthorizedSuccess: function (data) {
        return ({
            status_code: 200,
            message: {
                text: 'AuthorizedSuccess. Token was created.'
            },
            data: {},
            meta: {},
            token: data
        })
    }
};