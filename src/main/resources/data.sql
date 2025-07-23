INSERT INTO product (name, price, image_url)
VALUES ('아이템 1', 3000, 'https://placehold.co/100x100.png'),
       ('아이템 2', 3500, 'https://placehold.co/100x100.png');

-- 각 상품에 기본 옵션 추가
INSERT INTO option (name, quantity, product_id)
VALUES ('기본', 100, 1),
       ('기본', 100, 2);
