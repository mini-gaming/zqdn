DROP TABLE IF EXISTS `zqdn_user_info`;
CREATE TABLE `zqdn_user_info` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `OPEN_ID` varchar(255) NOT NULL,
  `NICKNAME` varchar(255) NULL,
  `GENDER` char(1) NULL,
  `CITY` varchar(255) NULL,
  `PROVINCE` varchar(255) NULL,
  `COUNTRY` varchar(255) NULL,
  `AVATAR_URL` varchar(255) NULL,
  `UNION_ID` varchar(255) NULL,
  `CRE_TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPD_TS` timestamp NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `OPEN_ID` (`OPEN_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `zqdn_user_game_map`;
CREATE TABLE `zqdn_user_game_map` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `OPEN_ID` varchar(255) NOT NULL,
  `GAME_ID` SMALLINT NOT NULL,
  `CHANNEL` TINYINT NOT NULL DEFAULT 0,
  `RCMND_OPEN_ID` varchar(255) NULL,
  `AUTH_USER_INFO` TINYINT NOT NULL DEFAULT 0,
  `AUTH_USER_LOCATION` TINYINT NOT NULL DEFAULT 0,
  `AUTH_ADDRESS` TINYINT NOT NULL DEFAULT 0,
  `AUTH_INVOICE_TITLE` TINYINT NOT NULL DEFAULT 0,
  `AUTH_WE_RUN` TINYINT NOT NULL DEFAULT 0,
  `AUTH_RECORD` TINYINT NOT NULL DEFAULT 0,
  `AUTH_WRITE_PHOTOS_ALBUM` TINYINT NOT NULL DEFAULT 0,
  `AUTH_CAMERA` TINYINT NOT NULL DEFAULT 0,
  `MAX_SCORE` int(11) NOT NULL DEFAULT 0,
  `CRE_TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPD_TS` timestamp NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `OPEN_ID_GAME_ID` (`OPEN_ID`,`GAME_ID`),
  INDEX `GAME_SCORE` (`GAME_ID`,`MAX_SCORE`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `zqdn_user_rltnshp_ntwk`;
CREATE TABLE `zqdn_user_rltnshp_ntwk` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `OPEN_ID_A` varchar(255) NOT NULL,
  `OPEN_ID_B` varchar(255) NOT NULL,
  `CRE_TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `OPEN_ID_AB` (`OPEN_ID_A`,`OPEN_ID_B`),
  INDEX `OPEN_ID_A` (`OPEN_ID_A`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `zqdn_game_login_instnc`;
CREATE TABLE `zqdn_game_login_instnc`(

  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GAME_ID` SMALLINT NOT NULL,
  `OPEN_ID` varchar(255) NOT NULL,
  `LOGIN_TIME` datetime NOT NULL,
  `LOGOUT_TIME` datetime NULL,
  `CRE_TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPD_TS` timestamp NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `zqdn_game_score_instnc`;
CREATE TABLE `zqdn_game_score_instnc` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GAME_ID` SMALLINT NOT NULL,
  `OPEN_ID` varchar(255) NOT NULL,
  `START_TIME` datetime NOT NULL,
  `END_TIME` datetime NOT NULL,
  `IS_FINISHED` TINYINT NOT NULL DEFAULT 0,
  `SCORE` int(11) NOT NULL DEFAULT 0,
  `CRE_TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `zqdn_game_meta`;
CREATE TABLE `zqdn_game_meta` (
  `ID` SMALLINT NOT NULL,
  `GAME_NAME` varchar(100) NOT NULL,
  `MAX_SCORE` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `GAME_NAME`(`GAME_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
insert into zqdn_game_meta (ID,GAME_NAME,MAX_SCORE) values (1,'华容道3*3',-21);
insert into zqdn_game_meta (ID,GAME_NAME,MAX_SCORE) values (2,'华容道4*4',-21);
insert into zqdn_game_meta (ID,GAME_NAME,MAX_SCORE) values (3,'字体迷宫',10);


ALTER TABLE zqdn_user_info CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE zqdn_user_info modify column NICKNAME varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `zqdn_game_idiom_dt`;
CREATE TABLE `zqdn_game_idiom_dt` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `IDIOM` varchar(100) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDIOM`(`IDIOM`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `zqdn_user_visit_log`;
CREATE TABLE `zqdn_user_visit_log` (
  `OPEN_ID` varchar(255) NOT NULL,
  `VISIT_DATE` date NOT NULL,
  `CRE_TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `VISIT_DATE` (`VISIT_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
/*!50100 PARTITION BY HASH (DAY(VISIT_DATE))
PARTITIONS 32 */;

/*Add a new game*/
insert into zqdn_game_meta (ID,GAME_NAME,MAX_SCORE) values (#{gameId},'字体迷宫',10);
insert into zqdn_user_game_map (
OPEN_ID,
GAME_ID,
CHANNEL,
RCMND_OPEN_ID,
AUTH_USER_INFO,
AUTH_USER_LOCATION,
AUTH_ADDRESS,
AUTH_INVOICE_TITLE,
AUTH_WE_RUN,
AUTH_RECORD,
AUTH_WRITE_PHOTOS_ALBUM,
AUTH_CAMERA)
(select 
OPEN_ID,
#{gameId},
CHANNEL,
RCMND_OPEN_ID,
AUTH_USER_INFO,
AUTH_USER_LOCATION,
AUTH_ADDRESS,
AUTH_INVOICE_TITLE,
AUTH_WE_RUN,
AUTH_RECORD,
AUTH_WRITE_PHOTOS_ALBUM,
AUTH_CAMERA
from zqdn_user_game_map 
where GAME_ID = 1);