Drop table IF EXISTS USER, CONFIRM;

/**
* 사용자
*/
CREATE TABLE `user` (
  `id` varchar(10) NOT NULL,
  `name` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `level` tinyint(2) DEFAULT NULL,
  `login` int(8) DEFAULT NULL,
  `recommend` int(8) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/**
* 사용자 불만 사항
* id : 접수 id
* confirm_date : 접수일
* confirm_seq : 순번(해당 아이디로, 해당 일자에 순번, 1부터 시작)
* confirm_time : 접수 시간
* content : 불만사항
* solve_content : 조치내용
* checkflagad : 불만 조치 완료 여부(관리자용)
* checkflagus : 불만 조치 확인 여부(사용자용)
*/
CREATE TABLE `CONFIRM` (
  `id` varchar(10) NOT NULL,
  `confirm_date` Date not null,
  `confirm_seq` Int(3) not null,
  `confirm_time` Time,
  `content` LONGTEXT DEFAULT NULL,
  `solve_content` LONGTEXT DEFAULT NULL,
  `checkflagad` varchar(1) default 'N',
  `checkflagus` varchar(1) default 'N',
  PRIMARY KEY (`id`, `confirm_date`, `confirm_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;