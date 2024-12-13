
CREATE TABLE if not exists `templates` (
  `node` varchar(255) NOT NULL,
  `name` varchar(4096) NOT NULL,
  `persistent` varchar(4096) NOT NULL DEFAULT '{}',
  `files` varchar(4096) NOT NULL DEFAULT '{}',
  `back_files` varchar(4096) NOT NULL DEFAULT '{}',
  PRIMARY KEY (`node`, `name`));

CREATE TABLE if not exists `groups` (
  `node` varchar(255) NOT NULL,
  `uuid` varchar(4096) NOT NULL,
  `name` varchar(4096) NOT NULL,
  `service_limit` int(4096) NOT NULL DEFAULT '1',
  `executable_file` varchar(4096) NOT NULL,
  `persistent` varchar(4096) NOT NULL DEFAULT '{}',
  `properties` varchar(4096) NOT NULL,
  `group_wrapper` varchar(4096),
  `templates` varchar(4096) NOT NULL DEFAULT '{}',
  PRIMARY KEY (`node`, `uuid`));

CREATE TABLE if not exists `services` (
  `node` varchar(255) NOT NULL,
  `uuid` varchar(4096) NOT NULL,
  `name` varchar(4096) NOT NULL,
  `group` varchar(4096) NOT NULL,
  `static` tinyint(4) NOT NULL DEFAULT '0',
  `persistent` varchar(4096) NOT NULL DEFAULT '{}',
  `created` bigint(4096) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`node`, `uuid`));