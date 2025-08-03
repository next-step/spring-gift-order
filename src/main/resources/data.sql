INSERT INTO product (name, price, image_url) VALUES ('hamburger', 4500, 'https://cdn.pixabay.com/photo/2022/07/15/18/12/cheese-burger-7323672_1280.jpg');
INSERT INTO product (name, price, image_url) VALUES ('coffee_latte', 5000, 'https://cdn.pixabay.com/photo/2017/12/05/11/39/coffee-2999161_1280.jpg');
INSERT INTO product (name, price, image_url) VALUES ('fried_chicken', 5000, 'https://cdn.pixabay.com/photo/2015/03/11/00/31/chicken-667935_640.jpg');

INSERT INTO member (email, password, role) VALUES ('ham@email.com', 'qwer123!', 'ADMIN');
INSERT INTO member (email, password, role) VALUES ('dam@email.com', 'qwer123!', 'USER');

INSERT INTO option (product_id, name, quantity) VALUES (1, '불고기버거', 10);
INSERT INTO option (product_id, name, quantity) VALUES (1, '치즈버거', 10);
INSERT INTO option (product_id, name, quantity) VALUES (2, '바닐라', 10);
INSERT INTO option (product_id, name, quantity) VALUES (2, '코코아', 10);
INSERT INTO option (product_id, name, quantity) VALUES (3, '간장치킨', 10);
INSERT INTO option (product_id, name, quantity) VALUES (3, '양념치킨', 10);

INSERT INTO wish (member_id, option_id, quantity) VALUES (2, 1, 1);
