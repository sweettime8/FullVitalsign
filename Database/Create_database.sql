
CREATE TABLE `hospital` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `address` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `hot_line` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Bệnh viện';

CREATE TABLE `account` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `username` varchar(30) COLLATE utf8_unicode_ci NOT NULL, 
  `email` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `mobile` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `full_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL ,
  `user_type` int(1) NOT NULL DEFAULT 1,
  `role_name` varchar(30) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'ADMIN' COMMENT 'từ 0 -> 100',
  `hospital_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Người dùng đăng nhập hệ thống';

CREATE TABLE `department` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `hospital_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Khoa';

CREATE TABLE `employee` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `account_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `department_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `full_name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `mobile` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Nhân viên y tế';


CREATE TABLE `room` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `department_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Phòng';

CREATE TABLE `patient` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `account_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `patient_code` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `full_name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `birth_date` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gender` varchar(1) COLLATE utf8_unicode_ci DEFAULT 'M' COMMENT 'M: male, F: female',
  `mobile` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `bed_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL,
  `additional_info` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Bệnh nhân';

CREATE TABLE `bed` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `room_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `note` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Giường';

CREATE TABLE `employee_patient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `patient_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `status` int(1) NOT NULL DEFAULT 1,
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Bảng map nhân viên y tế với bệnh nhân';

CREATE TABLE `sensor` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL,
  `patient_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `model` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sensor_type` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'V6' COMMENT '1 trong các giá trị: V6, BPHR, O2HR, ECG+',
  `mac` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rssi` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `serial_number` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `manufacture` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Xuất xứ, nhà sx',
  `firmware_version` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `battery_value` int(3) NOT NULL DEFAULT 0 COMMENT 'từ 0 -> 100',
  `status` int(1) NOT NULL DEFAULT 1,
  `measure_state` int(1) NOT NULL DEFAULT 0 COMMENT 'Trạng thái đo: 1: đang đo, 0: không đo',
  `measure_last_time` timestamp NULL DEFAULT NULL COMMENT 'Thời gian cuối hoạt động/đo',
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Thiết bị đo';


CREATE TABLE `gate` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
  `model` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `serial_number` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `manufacture` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Xuất xứ, nhà sx',
  `firmware_version` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `battery_value` int(3) NOT NULL DEFAULT 0 COMMENT 'từ 0 -> 100',
  `additional_info` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'JSON thông tin CSKH (email, sđt, .....)',
  `status` int(1) NOT NULL DEFAULT 1,
  `activity_state` int(1) NOT NULL DEFAULT 0 COMMENT 'Trạng thái hoạt động: 1: đang hoạt động, 0: không hoạt động',
  `activity_last_time` timestamp NULL DEFAULT NULL COMMENT 'Thời gian cuối hoạt động',
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Gate device (Android)';

CREATE TABLE `display` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL,
  `serial_number` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int NOT NULL DEFAULT '1',
  `is_deleted` int(1) NOT NULL DEFAULT 0,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Display';


CREATE TABLE `data_bp` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `display_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,  
  `sensor_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `measure_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'id phiên đo, dạng UUID',
  `ts` float NOT NULL,
  `dia` int(10) NOT NULL,  
  `sys` int(10) NOT NULL COMMENT 'chứa gia trị trong khoảng từ 1 -> 12\n (kênh)',
  `map` int(10) NOT NULL,
  `pr` int(10) DEFAULT NULL COMMENT 'Chỉ số nhịp tim (tương tự HR)',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Thời gian server insert row này',
  PRIMARY KEY (`id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Chứa dữ liệu đo của chỉ số BP (nhịp tim)'
PARTITION BY RANGE (UNIX_TIMESTAMP(created_at))
(PARTITION p_first VALUES LESS THAN (UNIX_TIMESTAMP('2020-12-02 00:00:00')),
 PARTITION p_2020_12_02 VALUES LESS THAN (UNIX_TIMESTAMP('2020-12-03 00:00:00')));
 
CREATE TABLE `data_bp_latest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `display_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,  
  `sensor_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `measure_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'id phiên đo, dạng UUID',
  `ts` float NOT NULL,
  `dia` int(10) NOT NULL,  
  `sys` int(10) NOT NULL COMMENT 'chứa gia trị trong khoảng từ 1 -> 12\n (kênh)',
  `map` int(10) NOT NULL,
  `pr` int(10) DEFAULT NULL COMMENT 'Chỉ số nhịp tim (tương tự HR)',
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT 'Thời gian server update row này',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Chứa dữ liệu đo của chỉ số BP "bản mới nhất"';


CREATE TABLE `data_spo2` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `display_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `sensor_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `measure_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'id phiên đo, dạng UUID',
  `ts` float NOT NULL,
  `spo2` int(10) NOT NULL,
  `pi` int(2) DEFAULT NULL,
  `pr` double DEFAULT NULL COMMENT 'Chỉ số nhịp tim (tương tự HR)',
  `step` int(10) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Thời gian server insert row này',
  PRIMARY KEY (`id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Chứa dữ liệu đo của chỉ số SPO2'
PARTITION BY RANGE (UNIX_TIMESTAMP(created_at))
(PARTITION p_first VALUES LESS THAN (UNIX_TIMESTAMP('2020-12-02 00:00:00')),
 PARTITION p_2020_12_02 VALUES LESS THAN (UNIX_TIMESTAMP('2020-12-03 00:00:00')));

CREATE TABLE `data_spo2_latest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `display_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `sensor_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `measure_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'id phiên đo, dạng UUID',
  `ts` float NOT NULL,
  `spo2` int(10) NOT NULL,
  `pi` int(2) DEFAULT NULL,
  `pr` double DEFAULT NULL COMMENT 'Chỉ số nhịp tim (tương tự HR)',
  `step` int(10) NOT NULL,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT 'Thời gian server update row này',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Chứa dữ liệu đo của chỉ số SPO2 "bản mới nhất"';


CREATE TABLE `data_temp` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `display_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `sensor_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `measure_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'id phiên đo, dạng UUID',
  `ts` float NOT NULL,
  `temp` float NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Thời gian server insert row này',
  PRIMARY KEY (`id`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Chứa dữ liệu đo của chỉ số nhiệt độ'
PARTITION BY RANGE (UNIX_TIMESTAMP(created_at))
(PARTITION p_first VALUES LESS THAN (UNIX_TIMESTAMP('2020-12-02 00:00:00')),
 PARTITION p_2020_12_02 VALUES LESS THAN (UNIX_TIMESTAMP('2020-12-03 00:00:00')));

CREATE TABLE `data_temp_latest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gate_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `display_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `sensor_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `measure_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'id phiên đo, dạng UUID',
  `ts` float NOT NULL,
  `temp` float NOT NULL,
  `last_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT 'Thời gian server update row này',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Chứa dữ liệu đo của chỉ số nhiệt độ "bản mới nhất"';







