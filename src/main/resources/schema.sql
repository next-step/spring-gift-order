Create table Product(
    id bigint AUTO_INCREMENT,
    name varchar(50),
    price int,
    image_url varchar(512),

    primary key (id)
);


Create table Member(
    id bigint AUTO_INCREMENT,
    email varchar(50),
    password varchar(255),
    role varchar(10),
    primary key (id)
);


CREATE TABLE Wish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT ,
    product_id BIGINT,
    quantity INT ,

    FOREIGN KEY (member_id) REFERENCES Member(id),
    FOREIGN KEY (product_id) REFERENCES Product(id)
);


