--INSERT INTO products (name, price, image_url, created_at)
--VALUES ('사과', 3000, 'apple.jpg', '2025-07-17 10:00:00');
--
--INSERT INTO products (name, price, image_url, created_at)
--VALUES ('바나나', 1500, 'banana.jpg', '2025-07-17 10:01:00');
--
--INSERT INTO products (name, price, image_url, created_at)
--VALUES ('포도', 4000, 'grape.jpg', '2025-07-17 10:02:00');

INSERT INTO products (id, name, price, image_url, created_at)
VALUES (1, '테스트 상품', 10000, 'http://image.url', NOW());

INSERT INTO product_option (id, product_id, name, quantity)
VALUES (1, 1, '검정색 / M', 10),
       (2, 1, '하얀색 / L', 5);