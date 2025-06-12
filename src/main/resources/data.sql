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

-- 国内航班 (2025年6月) - 已包含青岛航线
INSERT INTO `flight` (`departure_date`, `departure_time`, `flight_number`, `price`, `departure_airport_id`, `destination_airport_id`) VALUES
('2025-06-16', '08:30:00', 'CA1501', 1350.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'PVG')),  -- 北京→上海
('2025-06-16', '10:45:00', 'MU5102', 1050.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'CAN')),   -- 上海→广州
('2025-06-17', '14:20:00', 'CZ3108', 920.00, (SELECT id FROM airport WHERE code = 'CAN'), (SELECT id FROM airport WHERE code = 'CTU')),   -- 广州→成都
('2025-06-18', '16:10:00', 'HU7201', 810.00, (SELECT id FROM airport WHERE code = 'CTU'), (SELECT id FROM airport WHERE code = 'XIY')),   -- 成都→西安
('2025-06-19', '09:15:00', 'MF8103', 730.00, (SELECT id FROM airport WHERE code = 'XIY'), (SELECT id FROM airport WHERE code = 'HGH')),   -- 西安→杭州
('2025-06-20', '11:25:00', 'SC1177', 880.00, (SELECT id FROM airport WHERE code = 'TAO'), (SELECT id FROM airport WHERE code = 'CKG')),   -- 青岛→重庆
('2025-06-21', '13:40:00', '3U8021', 950.00, (SELECT id FROM airport WHERE code = 'KMG'), (SELECT id FROM airport WHERE code = 'SZX')),   -- 昆明→深圳
('2025-06-17', '07:45:00', 'QW9775', 650.00, (SELECT id FROM airport WHERE code = 'TAO'), (SELECT id FROM airport WHERE code = 'PEK')),   -- 青岛→北京
('2025-06-19', '16:30:00', 'CA1572', 720.00, (SELECT id FROM airport WHERE code = 'TAO'), (SELECT id FROM airport WHERE code = 'PVG')),   -- 青岛→上海
('2025-06-22', '09:20:00', 'SC4691', 580.00, (SELECT id FROM airport WHERE code = 'TAO'), (SELECT id FROM airport WHERE code = 'NKG')),   -- 青岛→南京

-- 国际航班 (2025年6月)
('2025-06-22', '13:25:00', 'CA983', 6200.00, (SELECT id FROM airport WHERE code = 'PEK'), (SELECT id FROM airport WHERE code = 'JFK')),    -- 北京→纽约
('2025-06-23', '15:40:00', 'BA038', 4500.00, (SELECT id FROM airport WHERE code = 'LHR'), (SELECT id FROM airport WHERE code = 'PVG')),    -- 伦敦→上海
('2025-06-24', '11:10:00', 'NH961', 3400.00, (SELECT id FROM airport WHERE code = 'HND'), (SELECT id FROM airport WHERE code = 'PEK')),    -- 东京→北京
('2025-06-25', '22:30:00', 'EK302', 4800.00, (SELECT id FROM airport WHERE code = 'DXB'), (SELECT id FROM airport WHERE code = 'CAN')),    -- 迪拜→广州
('2025-06-26', '08:55:00', 'SQ825', 4000.00, (SELECT id FROM airport WHERE code = 'SIN'), (SELECT id FROM airport WHERE code = 'HKG')),   -- 新加坡→香港
('2025-06-27', '17:20:00', 'QF107', 5200.00, (SELECT id FROM airport WHERE code = 'SYD'), (SELECT id FROM airport WHERE code = 'PVG')),   -- 悉尼→上海
('2025-06-28', '21:15:00', 'LH732', 3800.00, (SELECT id FROM airport WHERE code = 'FRA'), (SELECT id FROM airport WHERE code = 'PEK')),   -- 法兰克福→北京

-- 往返航班 (2025年6月)
('2025-06-29', '07:30:00', 'CA1502', 1250.00, (SELECT id FROM airport WHERE code = 'PVG'), (SELECT id FROM airport WHERE code = 'PEK')),  -- 上海→北京
('2025-06-30', '12:15:00', 'MU5103', 1100.00, (SELECT id FROM airport WHERE code = 'CAN'), (SELECT id FROM airport WHERE code = 'PVG')),  -- 广州→上海
('2025-07-01', '18:40:00', 'CZ3109', 980.00, (SELECT id FROM airport WHERE code = 'CTU'), (SELECT id FROM airport WHERE code = 'CAN')),   -- 成都→广州
('2025-07-02', '20:05:00', 'HU7202', 850.00, (SELECT id FROM airport WHERE code = 'XIY'), (SELECT id FROM airport WHERE code = 'CTU')),   -- 西安→成都
('2025-07-03', '10:50:00', 'MF8104', 790.00, (SELECT id FROM airport WHERE code = 'HGH'), (SELECT id FROM airport WHERE code = 'XIY'));   -- 杭州→西安
