CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL UNIQUE
);


CREATE TABLE users(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    role_id BIGINT NOT NULL,
    enabled TINYINT NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE staff(
	staff_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    post VARCHAR(25) NOT NULL,
    phone VARCHAR(32),
    date_begin DATE NOT NULL,
    date_end DATE,
    salary DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE food_groups(
	group_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    position INT NOT NULL,
    name VARCHAR(32)
);


CREATE TABLE menu_items(
	food_id BIGINT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(64) NOT NULL,
	group_id BIGINT NOT NULL,
	description VARCHAR(128),
	price DECIMAL(10, 2) NOT NULL,
	special_price DECIMAL(10, 2) NOT NULL,
	image VARCHAR(255),
	is_active BOOLEAN NOT NULL,
    is_archived BOOLEAN NOT NULL,
    status_change_time TIMESTAMP NOT NULL,
    FOREIGN KEY (group_id) REFERENCES food_groups(group_id)
);



CREATE TABLE discounts(
	discount_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    discount_name VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    discount_percent INT NOT NULL,
    min_sum DECIMAL(10, 2) NOT NULL,
    max_sum DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL
);


CREATE TABLE bonus_cards(
	card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_name VARCHAR(128) NOT NULL,
	client_phone VARCHAR(32) NOT NULL UNIQUE,
    client_email VARCHAR(64),
    time_register TIMESTAMP NOT NULL,
    discount_id BIGINT NOT NULL,
    total_sum DECIMAL(10, 2) NOT NULL DEFAULT '0.00',
    FOREIGN KEY (discount_id) REFERENCES discounts(discount_id)
);


CREATE TABLE receipt_statuses(
	receipt_status_id BIGINT PRIMARY KEY,
    status_name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE receipts(
	receipt_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    client_receipt_code VARCHAR(4) NOT NULL,
	open_time TIMESTAMP NOT NULL,
    close_time TIMESTAMP NULL,
    receipt_status_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    card_id BIGINT NULL,
    total_sum DECIMAL(10, 2),
    discount_sum DECIMAL(10, 2),
    final_sum DECIMAL(10, 2),
    received BOOLEAN NOT NULL,
    FOREIGN KEY (receipt_status_id) REFERENCES receipt_statuses(receipt_status_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
    FOREIGN KEY (card_id) REFERENCES bonus_cards(card_id)
);


CREATE TABLE order_statuses(
	order_status_id BIGINT PRIMARY KEY,
    status_name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE orders(
	order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    food_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    sum DECIMAL(10, 2) NOT NULL,
    order_status_id BIGINT NOT NULL,
	receipt_id BIGINT NOT NULL,
    staff_id BIGINT,
    FOREIGN KEY (receipt_id) REFERENCES receipts(receipt_id),
    FOREIGN KEY (food_id) REFERENCES menu_items(food_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
    FOREIGN KEY (order_status_id) REFERENCES order_statuses(order_status_id)
);