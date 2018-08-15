DROP TABLE IF EXISTS vote;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DELETE FROM INFORMATION_SCHEMA.SEQUENCES;

CREATE TABLE users (
  id               INTEGER AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR              NOT NULL,
  email            VARCHAR              NOT NULL,
  password         VARCHAR              NOT NULL,
  enabled          BOOLEAN DEFAULT TRUE NOT NULL,
  CONSTRAINT email_idx UNIQUE (email)
);

CREATE TABLE user_roles (
  user_id INTEGER NOT NULL,
  role    VARCHAR,
  CONSTRAINT user_roles_idx UNIQUE (user_id, role),
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE restaurants (
  id   INTEGER AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR NOT NULL,
  CONSTRAINT name_idx UNIQUE (name)
);

CREATE TABLE menu (
  id            INTEGER AUTO_INCREMENT PRIMARY KEY,
  date          DATE    NOT NULL,
  restaurant_id INTEGER NOT NULL,
  CONSTRAINT date_restaurant_idx UNIQUE (date, restaurant_id),
  FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE dishes (
  id      INTEGER AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR NOT NULL,
  price   INTEGER NOT NULL,
  menu_id INTEGER NOT NULL,
  CONSTRAINT name_price_menu_idx UNIQUE (name, price, menu_id),
  FOREIGN KEY (menu_id) REFERENCES menu (id) ON DELETE CASCADE
);

CREATE TABLE vote (
  id            INTEGER AUTO_INCREMENT PRIMARY KEY,
  date          DATE    NOT NULL,
  user_id       INTEGER NOT NULL,
  restaurant_id INTEGER NOT NULL,
  CONSTRAINT date_user_idx UNIQUE (date, user_id),
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);