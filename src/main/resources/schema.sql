CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(320) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER'
);

CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    image_url VARCHAR(2048),
    created_at DATETIME NOT NULL
);

CREATE TABLE wishes(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    CONSTRAINT uk_wishes_members_products UNIQUE (member_id, product_id),
    CONSTRAINT fk_wishes_members FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_wishes_products FOREIGN KEY (product_id) REFERENCES products(id)
)