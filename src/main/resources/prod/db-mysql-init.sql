
DROP TABLE IF EXISTS `gmo_cmpgn_timetable_emarsys`;
CREATE TABLE `gmo_cmpgn_timetable_emarsys` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(11) NOT NULL,
  `country` varchar(15) NOT NULL,
  `emarsys_id` varchar(60) NOT NULL,
  `cmpgn_code` varchar(24) NULL,
  `cmpgn_run_date` varchar(60) NULL,
  `full_id` varchar(60) NOT NULL,
  `rec` char(1) NOT NULL DEFAULT 'A',
  `sentdate` date NULL,
  `scheduled` datetime NOT NULL,
  `import_ready` datetime NULL,
  `send_start` datetime NULL,
  `send_end` datetime NULL,
  `status` varchar(25) NOT NULL,
  `import_status` varchar(25) NULL,
  `is_critical` bit(1) NOT NULL DEFAULT b'0',
  `imported_count` int(11) NOT NULL DEFAULT 0,
  `sent_volume` int(11) NOT NULL DEFAULT 0,
  `sparc_key` int(11) NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `scheduled` (`scheduled`),
  UNIQUE KEY `full_id` (`full_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

alter table gmo_cmpgn_timetable_emarsys add column chime_status_code varchar(25) NOT NULL DEFAULT 'N';
alter table gmo_cmpgn_timetable_emarsys add column chime_state_code varchar(25) NOT NULL DEFAULT 'N';
alter table gmo_cmpgn_timetable_emarsys add column chime_run_status varchar(25) NULL;
alter table gmo_cmpgn_timetable_emarsys add column chime_user_count  int(11) NOT NULL DEFAULT 0;
alter table gmo_cmpgn_timetable_emarsys add column chime_start_time datetime NULL;
alter table gmo_cmpgn_timetable_emarsys add column chime_end_time datetime NULL;
alter table gmo_cmpgn_timetable_emarsys add column file_name varchar(255) NULL;
alter table gmo_cmpgn_timetable_emarsys add column uc4_date varchar(25) NULL;

alter table gmo_cmpgn_timetable_emarsys add column uc4_start_time datetime NULL;
alter table gmo_cmpgn_timetable_emarsys add column uc4_end_time datetime NULL;
alter table gmo_cmpgn_timetable_emarsys add column uc4_run_status varchar(25) NULL;

insert into gmo_cmpgn_mdt (country,emarsys_id,rec,is_critical,sparc_id,create_time,update_time) select max(country),emarsys_id,MAX(rec),max(is_critical),max(sparc_key),max(create_time),max(update_time) from gmo_cmpgn_timetable_emarsys GROUP BY emarsys_id;

DROP TABLE IF EXISTS `gmo_cmpgn_timetable_chime`;
CREATE TABLE `gmo_cmpgn_timetable_chime` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(15) NOT NULL,
  `cmpgn_code` varchar(24) NOT NULL,
  `cmpgn_name` varchar(60) NOT NULL,
  `chime_name` varchar(255) NOT NULL,
  `cmpgn_owner` varchar(60) NULL,
  `channels` varchar(255) NULL,
  `sparc_id` varchar(255) NULL,
  `user_count` int(11) NOT NULL DEFAULT 0,
  `cmpgn_run_date` varchar(60) NULL,
  `rec` char(1) NOT NULL DEFAULT 'A',
  `scheduled` datetime NULL,
 `status` varchar(25) NULL,
  `s_code` varchar(25) NULL, --status code
  `status_code` varchar(25) NULL, --state code
  `freequency` varchar(25) NULL,
  `start_time` datetime NULL,
  `end_time` datetime NULL,
  `create_time` datetime NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cmpgn_unique` (`cmpgn_name`, `cmpgn_run_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `gmo_cmpgn_mdt`;
CREATE TABLE `gmo_cmpgn_mdt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(16) NULL,
  `emarsys_id` varchar(64) NOT NULL,
  `cmpgn_code` varchar(64) NULL,
  `chime_name` varchar(64) NULL,
  `cmpgn_owner` varchar(64) NULL,
  `channels` varchar(255) NULL,
  `rec` char(1) NOT NULL DEFAULT 'A',
  `is_critical` bit(1) NOT NULL DEFAULT b'0',
  `chime_scheduled` datetime NULL,
  `freequency` varchar(64) NULL,
  `is_monthly` bit(1) NOT NULL DEFAULT b'0',
  `monthly_view` text null,
  `monthly_percent` float(10,2) null,
  `sparc_id` int(11) NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `emarsys_id` (`emarsys_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sparc_gmo_dtl_w` (
  `auto_increment` decimal(10,0) DEFAULT NULL,
  `cal_dt` date DEFAULT NULL,
  `sparc_id` varchar(100) DEFAULT NULL,
  `ops_id` varchar(100) DEFAULT NULL,
  `cmpgn_nm` varchar(400) DEFAULT NULL,
  `cmpgn_mngr` varchar(200) DEFAULT NULL,
  `cntry_nm` varchar(200) DEFAULT NULL,
  `cmpgn_pod` varchar(200) DEFAULT NULL,
  `creative_mngr` varchar(200) DEFAULT NULL,
  `lts` varchar(60) DEFAULT NULL,
  `grp` varchar(60) DEFAULT NULL,
  `subgrp` varchar(200) DEFAULT NULL,
  `em_emarsys_id` varchar(3600) DEFAULT NULL,
  `dbm_cmc_name` varchar(6000) DEFAULT NULL,
  `ops_spclst` varchar(240) DEFAULT NULL,
  `em_spclst` varchar(200) DEFAULT NULL,
  `mbl_spclst` varchar(200) DEFAULT NULL,
  `om_spclst` varchar(200) DEFAULT NULL,
  `cpn_spclst` varchar(160) DEFAULT NULL,
  `admin_cmpgn` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1



DROP TABLE IF EXISTS `gmo_cmpgn_emarsys_report`;
CREATE TABLE `gmo_cmpgn_emarsys_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `full_id` varchar(60) NOT NULL,
  `launch_id` varchar(60) NOT NULL,
  `from_time` datetime NULL,
  `to_time` datetime NULL,
  `queued_emails` int(11) NOT NULL DEFAULT 0,
  `invalid_addresses` int(11) NOT NULL DEFAULT 0,
  `bounces_soft` int(11) NOT NULL DEFAULT 0,
  `bounces_hard` int(11) NOT NULL DEFAULT 0,
  `bounces_block` int(11) NOT NULL DEFAULT 0,
  `unsubscribes` int(11) NOT NULL DEFAULT 0,
  `clicks` int(11) NOT NULL DEFAULT 0,
  `spam_complaints` int(11) NOT NULL DEFAULT 0,
  `opens` int(11) NOT NULL DEFAULT 0,
  `opening_recipients` int(11) NOT NULL DEFAULT 0,
  `clicked_links` int(11) NOT NULL DEFAULT 0,
  `clicking_recipients` int(11) NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `full_id_launch_id` (`full_id`,`launch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `gmo_cmpgn_emarsys_launch`;
CREATE TABLE `gmo_cmpgn_emarsys_launch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `full_id` varchar(60) NOT NULL,
  `launch_id` varchar(60) NOT NULL,
  `launch_name` varchar(60) NOT NULL,
  `start_date` datetime NULL,
  `end_date` datetime NULL,
  `recipient_count` int(11) NOT NULL DEFAULT 0,
  `percent` int(11) NOT NULL DEFAULT 0,
  `state` varchar(60) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `launch_id` (`launch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cmpgn_whtlst_mdt`;
CREATE TABLE `cmpgn_whtlst_mdt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(18) NOT NULL,
  `create_by` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  `effective_time` datetime NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cmpgn_whtlst_admin_mdt`;
CREATE TABLE `cmpgn_whtlst_admin_mdt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(100) NOT NULL,
  `create_by` varchar(255) NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cmpgn_whtlst_site_mdt`;
CREATE TABLE `cmpgn_whtlst_site_mdt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site` varchar(16) NOT NULL,
  `site_id` varchar(255) NOT NULL DEFAULT '',
  `threshold` int(11) NOT NULL,
  `const` varchar(255) NOT NULL,
  `experiment_id` varchar(255) NOT NULL,
  `mod_value` int(11) NOT NULL DEFAULT 100,
  `status` varchar(16) NOT NULL DEFAULT 'E',
  PRIMARY KEY (`id`),
  UNIQUE KEY `site` (`site`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into cmpgn_whtlst_site_mdt (site,site_id,threshold,const,experiment_id) VALUES
('US','0,100',4,'EXPT','US_UB7_2016'),
('UK','3',4,'EXPT','UK_UB7_2016'),
('DE','77',4,'EXPT','DE_UB7_2016'),
('AU','15',4,'EXPT','AU_UB7_2016'),
('GLOBAL','',4,'EXPT','US_UB7_2016');
