'use strict';

module.exports = {

    /*Main server*/
    'SERVER_PORT': 4000,
	//'TLS_PRIVKEY': '/etc/apache2/ssl/videointerview.ga/private.key',
    //'TLS_FULLCHAIN': '/etc/apache2/ssl/videointerview.ga/certificate.crt',
    'TLS_PRIVKEY': 'ssl-key/privkey.pem',
    'TLS_FULLCHAIN': 'ssl-key/fullchain.pem',

    'MQTT_TLS_CA_CERT': 'ssl-key/ssl-mqtt-secure-file/ca.crt',
    'MQTT_TLS_CLIENT_CERT': 'ssl-key/ssl-mqtt-secure-file/client.crt',
    'MQTT_TLS_CLIENT_KEY': 'ssl-key/ssl-mqtt-secure-file/client.key',

    /* REDIS */
    /*'REDIS_HOST': 'localhost',
    'REDIS_PORT': 6379,
    'REDIS_PW': 'dev-eonline',
    'REDIS_USER_KEY': 'interview_maps_email_socketid',*/

    /* MYSQL */
    'DB_HOST': '103.21.151.171',
    'DB_PORT': 3306,
    'DB_SCHEMA': 'vitalsign',
    'DB_USER': 'vitalsign',
    'DB_PW': 'x9EdBetR8BrF',

    /*Jwt AUTH KEY*/
    'AUTH_TRANS_KEY': 'elcom_wq3Dr8O5wrkCSybDkQ==1_2020@)@)',

    /*MQTT*/
    'MQTT_PROTOCOL': 'mqtts',
    'MQTT_HOST': '103.21.151.131',
    'MQTT_PORT': 8883,

    'MQTT_TOPIC_DATA_ECG':       'VITALSIGN-TOPIC-DATA-ECG',
    'MQTT_TOPIC_DATA_BP':        'VITALSIGN-TOPIC-DATA-BP',
    'MQTT_TOPIC_DATA_SPO2':      'VITALSIGN-TOPIC-DATA-SPO2',
    'MQTT_TOPIC_DATA_SPO2_WAVE': 'VITALSIGN-TOPIC-DATA-SPO2-WAVE',
    'MQTT_TOPIC_DATA_HR_RR':        'VITALSIGN-TOPIC-DATA-HR-RR',
    //'MQTT_TOPIC_DATA_RR':        'VITALSIGN-TOPIC-DATA-RR',
    'MQTT_TOPIC_DATA_TEMP':      'VITALSIGN-TOPIC-DATA-TEMP',

    'QUEUE_MAX_ITEMS': 3
};