CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(320) UNIQUE,
    password VARCHAR(255),
    login_type VARCHAR(50),
    social_id VARCHAR(100),
    role VARCHAR(50) DEFAULT 'USER',
    access_token VARCHAR(2000)
);


CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    image_url VARCHAR(2048),
    created_at DATETIME NOT NULL
);

CREATE TABLE product_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    quantity BIGINT NOT NULL,
    CONSTRAINT fk_product_option_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE wishes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    CONSTRAINT uk_wishes_members_options UNIQUE (member_id, option_id),
    CONSTRAINT fk_wishes_members FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_wishes_options FOREIGN KEY (option_id) REFERENCES product_option(id)
);


CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    option_name VARCHAR(255) NOT NULL,
    option_price INT NOT NULL,
    quantity INT NOT NULL,
    message VARCHAR(1000),
    order_date_time DATETIME NOT NULL,
    CONSTRAINT fk_orders_member
        FOREIGN KEY (member_id)
        REFERENCES members (id)
);