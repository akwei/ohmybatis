CREATE DATABASE `db_ohmybatis` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;

CREATE TABLE `db_ohmybatis`.`t_user` (
  `userid` bigint(20) NOT NULL AUTO_INCREMENT,
  `nick` varchar(45) NOT NULL,
  `sex` int(10) DEFAULT NULL,
  `addr` varchar(300) NOT NULL,
  `intro` varchar(300) DEFAULT NULL,
  `enableflag` tinyint(1) NOT NULL,
  `level` int(11) NOT NULL,
  `createtime` datetime NOT NULL,
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
