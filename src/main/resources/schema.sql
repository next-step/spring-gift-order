CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price INT NOT NULL,
                         image_url VARCHAR(1000)
);

CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
);

CREATE TABLE product_option (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                name VARCHAR(50) NOT NULL,
                                quantity INT NOT NULL,
                                CONSTRAINT fk_product_option_product
                                    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE wish (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      member_id BIGINT NOT NULL,
                      product_id BIGINT NOT NULL,
                      option_id BIGINT,
                      CONSTRAINT fk_wish_member FOREIGN KEY (member_id) REFERENCES member(id),
                      CONSTRAINT fk_wish_product FOREIGN KEY (product_id) REFERENCES product(id),
                      CONSTRAINT fk_wish_option FOREIGN KEY (option_id) REFERENCES product_option(id)

);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        option_id BIGINT NOT NULL,
                        quantity INT NOT NULL,
                        order_date_time TIMESTAMP NOT NULL,
                        message VARCHAR(1000),
                        CONSTRAINT fk_orders_option FOREIGN KEY (option_id) REFERENCES product_option(id)
);
