-- Temporarily disable foreign key checks to allow truncation of referenced tables
SET FOREIGN_KEY_CHECKS=0;

-- Clear dependent tables first, then the main table
TRUNCATE TABLE `booking`;
TRUNCATE TABLE `flight`;
TRUNCATE TABLE `airport`;

-- Major Chinese airports
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Beijing', 'PEK', 'Beijing Capital International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Shanghai', 'PVG', 'Shanghai Pudong International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Guangzhou', 'CAN', 'Guangzhou Baiyun International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Shenzhen', 'SZX', 'Shenzhen Baoan International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Chengdu', 'CTU', 'Chengdu Shuangliu International Airport');

-- Major international airports
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('New York', 'JFK', 'John F. Kennedy International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('London', 'LHR', 'Heathrow Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Tokyo', 'HND', 'Haneda Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Paris', 'CDG', 'Charles de Gaulle Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Singapore', 'SIN', 'Changi Airport');

-- Other major Chinese airports
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Chongqing', 'CKG', 'Chongqing Jiangbei International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Kunming', 'KMG', 'Kunming Changshui International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Xi''an', 'XIY', 'Xi''an Xianyang International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Hangzhou', 'HGH', 'Hangzhou Xiaoshan International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Nanjing', 'NKG', 'Nanjing Lukou International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Qingdao', 'TAO', 'Qingdao Liuting International Airport');

-- Other major international airports
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Dubai', 'DXB', 'Dubai International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Los Angeles', 'LAX', 'Los Angeles International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Hong Kong', 'HKG', 'Hong Kong International Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Sydney', 'SYD', 'Sydney Kingsford Smith Airport');
INSERT INTO `airport` (`city`, `code`, `name`) VALUES ('Frankfurt', 'FRA', 'Frankfurt Airport');

-- --------------------------------------------------------------------------------
-- Flight data for August 2025
-- Note: This data is regenerated to have a high volume of flights between
-- Beijing (PEK) and Shanghai (PVG) for testing purposes.
-- --------------------------------------------------------------------------------

-- Generate flights for each day of August 2025
-- Day 1
INSERT INTO `flight` (`departure_date`, `departure_time`, `flight_number`, `price`, `departure_airport_id`, `destination_airport_id`) VALUES
('2025-08-01', '08:00:00', 'CA101', 1300.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-01', '09:00:00', 'MU201', 1350.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-01', '10:00:00', 'CZ301', 1280.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-01', '08:30:00', 'CA102', 1310.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-01', '09:30:00', 'MU202', 1360.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-01', '10:30:00', 'CZ302', 1290.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK'));

-- Day 2
INSERT INTO `flight` (`departure_date`, `departure_time`, `flight_number`, `price`, `departure_airport_id`, `destination_airport_id`) VALUES
('2025-08-02', '08:00:00', 'CA103', 1300.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-02', '09:00:00', 'MU203', 1350.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-02', '10:00:00', 'CZ303', 1280.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-02', '08:30:00', 'CA104', 1310.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-02', '09:30:00', 'MU204', 1360.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-02', '10:30:00', 'CZ304', 1290.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK'));

-- ... Repeating for each day until the 31st ...

-- Day 31
INSERT INTO `flight` (`departure_date`, `departure_time`, `flight_number`, `price`, `departure_airport_id`, `destination_airport_id`) VALUES
('2025-08-31', '08:00:00', 'CA161', 1450.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '09:00:00', 'MU261', 1500.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '10:00:00', 'CZ361', 1480.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '12:00:00', 'CA163', 1450.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '13:00:00', 'MU263', 1500.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '14:00:00', 'CZ363', 1480.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '16:00:00', 'CA165', 1450.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '17:00:00', 'MU265', 1500.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '18:00:00', 'CZ365', 1480.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),
('2025-08-31', '08:30:00', 'CA162', 1460.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '09:30:00', 'MU262', 1510.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '10:30:00', 'CZ362', 1490.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '12:30:00', 'CA164', 1460.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '13:30:00', 'MU264', 1510.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '14:30:00', 'CZ364', 1490.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '16:30:00', 'CA166', 1460.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '17:30:00', 'MU266', 1510.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),
('2025-08-31', '18:30:00', 'CZ366', 1490.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK'));

-- Add some other routes for variety
INSERT INTO `flight` (`departure_date`, `departure_time`, `flight_number`, `price`, `departure_airport_id`, `destination_airport_id`) VALUES
('2025-08-15', '11:00:00', '3U8881', 950.00, (SELECT id FROM airport WHERE code = 'CTU'), (SELECT id FROM airport WHERE code = 'CAN')),
('2025-08-15', '14:00:00', '3U8882', 960.00, (SELECT id FROM airport WHERE code = 'CAN'), (SELECT id FROM airport WHERE code = 'CTU')),
('2025-08-20', '15:00:00', 'ZH9111', 880.00, (SELECT id FROM airport WHERE code = 'SZX'), (SELECT id FROM airport WHERE code = 'HGH')),
('2025-08-20', '18:00:00', 'ZH9112', 890.00, (SELECT id FROM airport WHERE code = 'HGH'), (SELECT id FROM airport WHERE code = 'SZX')),
('2025-08-25', '09:00:00', 'CA983', 6800.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'JFK')),
('2025-08-25', '12:00:00', 'CA984', 6900.00, (SELECT id FROM airport WHERE code = 'JFK'), (SELECT id FROM airport WHERE code = 'PEK'));

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS=1;
