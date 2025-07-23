DROP TABLE IF EXISTS wishlists;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS members;

create table products
(
    id        bigint auto_increment primary key,
    name      varchar(255)   NOT NULL,
    price     decimal(10, 2) NOT NULL,
    image_url varchar(512)
);

create table members
(
    id       bigint auto_increment primary key,
    email    varchar(255) NOT NULL unique,
    password varchar(255) NOT NULL
);

create table wishlists
(
    id         bigint auto_increment primary key,
    member_id  bigint,
    product_id bigint,
    quantity   int
);

create table options
(
    id         bigint auto_increment primary key,
    name       varchar(50),
    product_id bigint,
    quantity   int
);

alter table if exists wishlists
    add constraint fk_wish_member_id_ref_member_id
    foreign key (member_id)
    references members;

alter table if exists wishlists
    add constraint fk_wish_product_id_ref_product_id
    foreign key (product_id)
    references products;

alter table if exists options
    add constraint fk_option_product_id_ref_product_id
    foreign key (product_id)
    references products;