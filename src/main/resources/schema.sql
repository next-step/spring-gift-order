CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price BIGINT NOT NULL,
                         image_url VARCHAR(512) NOT NULL
);

CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
);

-- id 컬럼 삭제, member_id와 product_id를 복합 기본 키�� 설정
CREATE TABLE wish (
                      member_id BIGINT NOT NULL,
                      product_id BIGINT NOT NULL,
                      quantity INT NOT NULL DEFAULT 1,
                      PRIMARY KEY (member_id, product_id),
                      FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                      FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE option (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        quantity INT NOT NULL,
                        product_id BIGINT NOT NULL,
                        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
                        UNIQUE (product_id, name)
);
