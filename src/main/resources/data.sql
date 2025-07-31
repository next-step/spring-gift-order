insert into product(name, price, quantity, image_url)
values ('example1', 4700, 2, 'https://www.starbucks.co.kr/index.do');
insert into product_option(name, quantity, product_id)
values ('option1', 2, 1);

insert into product(name, price, quantity, image_url)
values ('example2', 5200, 4, 'https://www.starbucks.co.kr/index.do');
insert into product_option(name, quantity, product_id)
values ('option2', 4, 2);

insert into product(name, price, quantity, image_url)
values ('example3', 5900, 9, 'https://www.starbucks.co.kr/index.do');
insert into product_option(name, quantity, product_id)
values ('option3', 9, 3);

insert into member(email, password, login_type)
values ('example@naver.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'LOCAL');