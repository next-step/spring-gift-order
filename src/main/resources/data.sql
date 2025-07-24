-- users 데이터
INSERT INTO users (email, password, nickname) VALUES
('s@s', '$2a$10$I8HkGQtGiBcz8PR.xahV.uj96YCqL1pJhZk6t9cR70G.v.TdKp/vC', '이시웅'),
('t@t', '$2a$10$I8HkGQtGiBcz8PR.xahV.uj96YCqL1pJhZk6t9cR70G.v.TdKp/vC', '카카오');

-- item 데이터 (user_id 포함)
INSERT INTO item (name, price, image_url, user_id) VALUES
('item1',  1000,  'url1',  1),
('item2',  2000,  'url2',  1),
('item3',  3000,  'url3',  1),
('item4',  4000,  'url4',  1),
('item5',  5000,  'url5',  1),
('item6',  6000,  'url6',  1),
('item7',  7000,  'url7',  1),
('item8',  8000,  'url8',  1),
('item9',  9000,  'url9',  2),
('item10',10000, 'url10', 2),
('item11',11000, 'url11', 2),
('item12',12000, 'url12', 2),
('item13',13000, 'url13', 2),
('item14',14000, 'url14', 2),
('item15',15000, 'url15', 2);

INSERT INTO wish_list (user_id, item_id, amount) VALUES
 (1, 1, 1),
 (1, 2, 1),
 (1, 3, 1),
 (1, 4, 1),
 (1, 5, 1),
 (2, 1, 1),
 (2, 2, 1),
 (2, 3, 1),
 (2, 4, 1),
 (2, 5, 1),
 (2, 6, 1),
 (2, 7, 1);

INSERT INTO option (option_name, quantity, item_id) VALUES
('옵션1', 1, 1),
('옵션2', 1, 1),
('옵션3', 1, 1);
