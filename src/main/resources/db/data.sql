DELETE FROM vote;
DELETE FROM dishes;
DELETE FROM menu;
DELETE FROM restaurants;
DELETE FROM user_roles;
DELETE FROM users;

ALTER TABLE vote ALTER COLUMN id RESTART WITH 1;
ALTER TABLE dishes ALTER COLUMN id RESTART WITH 1;
ALTER TABLE menu ALTER COLUMN id RESTART WITH 1;
ALTER TABLE restaurants ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (id, name, email, password) VALUES
  (100000, 'User', 'user@yandex.ru', 'password'),
  (100001, 'Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001),
  ('ROLE_USER', 100001);

INSERT INTO restaurants (id, name) VALUES
  (100000, 'Манана'),
  (100001, 'Arcobaleno'),
  (100002, 'Cosmo');

INSERT INTO menu (id, date, restaurant_id) VALUES
  (100000, '2017-05-20', 100000), (100001, '2018-08-14', 100000),
  (100002, '2017-05-20', 100001), (100003, '2018-08-14', 100001),
  (100004, '2017-05-20', 100002), (100005, '2018-08-14', 100002);

INSERT INTO dishes (id, name, price, menu_id) VALUES
  (100000, 'Старое пиво', 540, 100000), (100001, 'Старый салат', 1200, 100000),
  (100002, 'Пиво', 600, 100001), (100003, 'Салат', 1450, 100001),
  (100004, 'Старый хлеб', 200, 100002), (100005, 'Старый шашлык', 4500, 100002),
  (100006, 'Хлеб', 350, 100003), (100007, 'Шашлык', 5000, 100003),
  (100008, 'Старая семга', 1300, 100004), (100009, 'Старый сок', 400, 100004),
  (100010, 'Семга', 1400, 100005), (100011, 'Сок', 450, 100005);

INSERT INTO vote (id, date, user_id, restaurant_id) VALUES
  (100000, '2017-05-20', 100000, 100001), (100001, '2017-05-20', 100001, 100002),
  (100002, '2018-07-13', 100000, 100002), (100003, '2018-07-13', 100001, 100002);