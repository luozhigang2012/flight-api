-- Tables must be dropped in this order: passenger->booking->user->flight->airport
drop table IF EXISTS flight_db.passenger;
drop table IF EXISTS flight_db.booking;
drop table IF EXISTS flight_db.`user`;
drop table IF EXISTS flight_db.flight;
drop table IF EXISTS flight_db.airport;

-- Tables must be created in this order: airport->flight->user->booking->passenger
CREATE TABLE IF NOT EXISTS `airport` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(100) NOT NULL,
  `code` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkpeoje7ewxy99k1wt4cmttjgd` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `flight` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `departure_date` date NOT NULL,
  `departure_time` time(6) NOT NULL,
  `flight_number` varchar(10) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `departure_airport_id` bigint NOT NULL,
  `destination_airport_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKillsy04237nltbk2yryrbderb` (`departure_airport_id`),
  KEY `FK6uc5h994cl1g7yxsvnxkilqbl` (`destination_airport_id`),
  CONSTRAINT `FK6uc5h994cl1g7yxsvnxkilqbl` FOREIGN KEY (`destination_airport_id`) REFERENCES `airport` (`id`),
  CONSTRAINT `FKillsy04237nltbk2yryrbderb` FOREIGN KEY (`departure_airport_id`) REFERENCES `airport` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `country` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_time` datetime(6) NOT NULL,
  `reference` varchar(20) NOT NULL,
  `status` enum('PAST','UPCOMING') NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `flight_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6qyxfsr6xbajdkhybqh1wse8d` (`reference`),
  KEY `FK546eybei9q7dsna94vryofrbr` (`flight_id`),
  KEY `FKkgseyy7t56x7lkjgu3wah5s3t` (`user_id`),
  CONSTRAINT `FK546eybei9q7dsna94vryofrbr` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`),
  CONSTRAINT `FKkgseyy7t56x7lkjgu3wah5s3t` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `passenger` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `booking_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtco0omesfld1qi5sw76eomvt4` (`booking_id`),
  CONSTRAINT `FKtco0omesfld1qi5sw76eomvt4` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
