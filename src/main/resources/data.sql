INSERT INTO products (id, name, price, image_url, created_at)
VALUES (1, '테스트 상품', 10000, 'http://image.url', '2025-07-29 10:00:00');

INSERT INTO product_option (id, product_id, name, quantity)
VALUES
  (1, 1, '검정색 / M', 10),
  (2, 1, '하얀색 / L', 5);
