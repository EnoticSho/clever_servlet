--liquibase formatted sql

--changeset sergey:1
CREATE TABLE products
(
    id            UUID PRIMARY KEY,
    name          VARCHAR(255),
    price         DOUBLE PRECISION,
    weight        DOUBLE PRECISION,
    creation_date TIMESTAMP
);

--changeset sergey:2
INSERT INTO products (id, name, price, weight, creation_date)
VALUES ('dcce95ba-46ea-4739-887b-1de051755ac7', 'ProductA', 428.26, 3.79, '2023-11-13 03:09:02'),
       ('1d9411b4-53cc-42fc-8eeb-ab5d4c3820ba', 'ProductB', 79.67, 8.84, '2023-11-13 03:09:02'),
       ('c2d60cf9-006d-452d-b0fb-ca70f78d2102', 'ProductC', 360.07, 16.96, '2023-11-13 03:09:02'),
       ('50fd3bc5-06e1-4e29-a441-4d91ed404e3a', 'ProductD', 195.98, 9.75, '2023-11-13 03:09:02'),
       ('540cdb32-ace5-4b81-9388-5a4dc9c479cf', 'ProductE', 482.51, 17.81, '2023-11-13 03:09:02'),
       ('1bcf2c7a-9778-4887-b5ef-37b3140d278f', 'ProductF', 473.25, 14.12, '2023-11-13 03:09:02'),
       ('7c934f03-e58e-4b76-9a1f-398466ee3785', 'ProductJ', 58.39, 5.31, '2023-11-13 03:09:02'),
       ('96d42a47-f9b9-4b45-b1fb-267db5e6a342', 'ProductH', 379.51, 16.95, '2023-11-13 03:09:02'),
       ('0868cce8-ba84-4d61-a8d4-b96f5e64d1b5', 'ProductI', 412.29, 2.35, '2023-11-13 03:09:02'),
       ('8a5e82ac-731b-4dc4-b861-4c38cca887e3', 'ProductL', 162.03, 11.44, '2023-11-13 03:09:02');
