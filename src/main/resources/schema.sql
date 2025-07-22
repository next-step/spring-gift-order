
CREATE TABLE product(id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     name VARCHAR(255),
                     price BIGINT,
                     imageUrl VARCHAR(255));

CREATE TABLE "user"(id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) UNIQUE,
                    password VARCHAR(255),
                    created_date TIMESTAMP NOT NULL,
                    role VARCHAR(255));

CREATE TABLE wish(id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
                  product_id BIGINT REFERENCES product(id) ON DELETE CASCADE,
                  quantity BIGINT);

CREATE TABLE "option"(id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255));

CREATE TABLE product_option(id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
                            option_id BIGINT NOT NULL REFERENCES "option"(id) ON DELETE CASCADE,
                            "value" BIGINT);