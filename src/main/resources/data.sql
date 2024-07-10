INSERT INTO roles (name)
VALUES
('ROLE_ADMIN'),
('ROLE_MANAGER'),
('ROLE_CHEF'),
('ROLE_CASHIER'),
('ROLE_CLIENT');

-- {bcrypt} 5
INSERT INTO users (username, password, role_id, enabled)
VALUES
('admin', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 1, 1),
('boss', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 2, 1),
('chef', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 3, 1),
('chef1', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 3, 0),
('cashier', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 4, 1),
('cashier1', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 4, 1),
('cashier2', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 4, 0),
('client', '$2a$05$l8bfAaLcKYQDXVzY7pA6Jec9D6th0y.9yqYjZasZ.IR0lll9tkhFK', 5, 1);

 /* СИСТЕМА БОНУСОВ */
INSERT INTO discounts (discount_name, description, discount_percent, min_sum, max_sum, active)
VALUES
('Silver % ⭐', 'Стартовая скидка 5%', 5, '0.00', '3000.00', '1'),
('Gold % ⭐⭐', 'Скидка 10% (сумма покупок от 3000)', 10, '3000.00', '7000.00', '1'),
('Platinum % ⭐⭐⭐', 'Скидка 15% (сумма покупок от 7000)', 15, '7000.00', '15000.00', '1'),
('Brilliant % ⭐⭐⭐⭐', 'VIP скидка 30%', 30, '15000.00', '10000000.00', '1');

INSERT INTO bonus_cards (client_name, client_phone, client_email, time_register, discount_id, total_sum)
VALUES
('John Doe', '5550101', 'johndoe@example.com', '2024-02-01 10:00:00', 1, 1000.00),
('Jane Smith', '5550102', 'janesmith@example.com', '2024-02-03 11:00:00', 2, 1500.00),
('Alice Johnson', '5550103', 'alicej@example.com', '2024-02-22 12:00:00', 3, 1200.00),
('Bob Brown', '5550104', 'bobbrown@example.com', '2024-03-08 13:00:00', 1, 1100.00),
('Charlie Davis', '5550105', 'charlied@example.com', '2024-03-08 14:00:00', 2, 1300.00),
('Diana Evans', '5550106', 'dianae@example.com', '2024-04-12 15:00:00', 3, 1400.00),
('Frank Green', '5550107', 'frankg@example.com', '2024-05-03 16:00:00', 1, 1600.00),
('Grace Hall', '5550108', 'graceh@example.com', '2024-05-05 17:00:00', 2, 1700.00),
('Henry King', '5550109', 'henryk@example.com', '2024-05-21 18:00:00', 3, 1800.00),
('Isabella Lewis', '5550110', 'isabellal@example.com', '2024-05-30 19:00:00', 1, 1900.00);


/* ПЕРСОНАЛ */
INSERT INTO staff (user_id, name, post, phone, salary, date_begin, date_end)
VALUES
('2', 'Никита Кутузов', 'менеджер', '+7-978-256-48-97', 70000.00, '2024-01-01', NULL),
('3', 'Миша Лен', 'шеф-повар', '+7-978-359-78-12', 50000.00, '2024-01-01', NULL),
('4', 'Санджи Сан', 'повар', '+7-978-578-92-11', 50000.00, '2024-01-01', '2024-05-05'),
('5', 'Катя', 'кассир', '+7-978-846-78-75', 25000.00, '2024-01-01', NULL),
('6', 'Лёша', 'кассир', '+7-978-159-57-81', 25000.00, '2024-01-01', NULL),
('7', 'Маша', 'кассир', '+7-978-587-25-46', 25000.00, '2024-01-01', '2024-05-06');

/* МЕНЮ */
INSERT INTO food_groups (position, name)
VALUES
(3, 'Напитки'),
(2, 'Десерты'),
(1, 'Салаты'),
(0, 'Бургеры');


INSERT INTO menu_items (name, group_id, description, price, special_price, image, is_active, is_archived, status_change_time)
VALUES
('Чай "Ройбуш"', 1, 'Красный чай', 100.00, 100.00, '/img/food/tea.jpg', 1, 0, '2024-05-01 09:00:00'),
('Чай "Пуэр"', 1, 'Постферментированный чай', 120.00, 100.00, '/img/food/tea.jpg', 1, 0, '2024-05-01 09:00:00'),
('Чай "White"', 1, 'Белый чай', 120.00, 100.00, '/img/food/tea.jpg', 1, 0, '2024-05-01 09:00:00'),
('Эклер "Ванильное наслаждение"', 2, 'С ванильным кремом', 220.00, 200.00, '/img/food/eclair.jpg', 1, 0, '2024-05-01 09:00:00'),
('Эклер "Фисташковая мечта"', 2, 'С фисташковым кремом', 220.00, 200.00, '/img/food/eclair.jpg', 1, 0, '2024-05-01 09:00:00'),
('Эклер "Клубничное искушение"', 2, 'С клубничным кремом', 220.00, 200.00, '/img/food/eclair.jpg', 0, 0, '2024-05-01 09:00:00'),
('Эклер "Шоколадная фантазия"', 2, 'С шоколадным кремом', 220.00, 200.00, '/img/food/eclair.jpg', 1, 0, '2024-05-01 09:00:00'),
('Ананасовый тарт татен', 2, 'Пирог с ананасом a-la Bree Hodge', 140.00, 140.00, '/img/food/tart.jpg', 0, 0, '2024-05-01 09:00:00'),
('Салат "Цезарь" классика', 3, 'Классический', 400.00, 380.00, '/img/food/salad.jpg', 1, 0, '2024-05-04 09:00:00'),
('Салат "Цезарь" сёмга', 3, 'C сёмгой', 450.00, 450.00, '/img/food/salad.jpg', 1, 0, '2024-05-04 09:00:00'),
('Салат "Цезарь" бекон', 3, 'C беконом', 400.00, 380.00, '/img/food/salad.jpg', 1, 0, '2024-05-04 09:00:00'),
('Гамбургер', 4, 'с мраморной говядиной, сыром и свежими овощами', 450.00, 370.00, '/img/food/burger.jpg', 1, 0, '2024-05-04 09:00:00'),
('Чизбургер', 4, 'с двойным сыром чеддер', 450.00, 370.00, '/img/food/burger.jpg', 1, 0, '2024-05-04 09:00:00'),
('Чикенбургер', 4, 'с сочной курочкой', 450.00, 370.00, '/img/food/burger.jpg', 1, 0, '2024-05-04 09:00:00'),
('Двойной бургер', 4, 'с двумя котлетами', 580.00, 580.00, '/img/food/burger.jpg', 1, 0, '2024-05-04 09:00:00'),
('Фреш', 1, 'стакан свежевыжатого сока', 200.00, 200.00, '/img/food/juice.jpg', 1, 0, '2024-05-08 09:00:00');

/* ЧЕКИ И ЗАКАЗЫ */
INSERT INTO receipt_statuses (receipt_status_id, status_name)
VALUES
(1, 'OPEN'),
(2, 'READY TO PAY'),
(3, 'PAID'),
(4, 'CLOSED');


INSERT INTO order_statuses (order_status_id, status_name)
VALUES
(1, 'FORMING'),
(2, 'IN PROGRESS'),
(3, 'READY'),
(4, 'CANCELLED');


INSERT INTO receipts (client_receipt_code, open_time, close_time, receipt_status_id, staff_id, card_id, total_sum, discount_sum, final_sum, received)
VALUES
('A006', '2024-02-25 09:20:05', '2024-02-25 09:21:35', '4', '4', NULL, '150.00', '10.00', '135.00', 1),
('A020', '2024-02-25 10:00:05', '2024-02-25 10:01:35', '4', '4', '1', '540.00', '30.00', '510.00', 0),
('A013', '2024-05-25 11:05:45', '2024-05-25 11:08:12', '3', '4', NULL, '370.00', '15.00', '355.00', 0),
('A029', '2024-05-25 12:40:22', '2024-05-25 12:40:22', '3', '5', NULL, '370.00', '15.00', '355.00', 0),
('A034', '2024-06-25 10:00:05', '2024-06-25 12:40:22', '3', '5', '2', '520.00', '520.00', '0.00', 0);


INSERT INTO orders (food_id, price, quantity, sum, order_status_id, receipt_id, staff_id)
VALUES
(2, 220.00, 2, 440.00, 3, 1, 1),
(4, 400.00, 3, 1200.0, 3, 1, 1),
(6, 200.00, 3, 600.00, 3, 1, 1),
(2, 220.00, 2, 440.00, 3, 2, 1),
(4, 400.00, 2, 800.00, 3, 2, 1),
(5, 400.00, 2, 800.00, 3, 2, 1),
(1, 100.00, 2, 200.00, 2, 3, null),
(2, 220.00, 2, 440.00, 2, 3, null),
(5, 400.00, 5, 2000.0, 2, 3, null),
(6, 200.00, 3, 600.00, 2, 3, null),
(1, 100.00, 2, 200.00, 2, 4, null),
(2, 220.00, 2, 440.00, 2, 4, null),
(5, 400.00, 5, 2000.0, 2, 4, null),
(6, 200.00, 3, 600.00, 2, 4, null),
(1, 100.00, 2, 200.00, 2, 5, null),
(2, 220.00, 2, 440.00, 2, 5, null),
(5, 400.00, 5, 2000.0, 2, 5, null),
(6, 200.00, 3, 600.00, 2, 5, null);