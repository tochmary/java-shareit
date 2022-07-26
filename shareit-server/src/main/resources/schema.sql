DROP TABLE IF EXISTS requests, comments, bookings, items, users;

--Таблица пользователей
CREATE TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_email_uq UNIQUE (email)
);

--Таблица вещей
CREATE TABLE IF NOT EXISTS items
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_available BOOLEAN,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT items_pk PRIMARY KEY (id),
    CONSTRAINT items_users_id_fk FOREIGN KEY (owner_id) REFERENCES users (id)
);

--Таблица бронирований
CREATE TABLE IF NOT EXISTS bookings
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR(30),
    CONSTRAINT bookings_pk PRIMARY KEY (id),
    CONSTRAINT bookings_items_id_fk FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT bookings_users_id_fk FOREIGN KEY (booker_id) REFERENCES users (id)
);

--Таблица отзывов
CREATE TABLE IF NOT EXISTS comments
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(3000),
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT comments_pk PRIMARY KEY (id),
    CONSTRAINT comments_items_id_fk FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT comments_users_id_fk FOREIGN KEY (author_id) REFERENCES users (id)
);

--Таблица запросов
CREATE TABLE IF NOT EXISTS requests
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(1000),
    requestor_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT requests_pk PRIMARY KEY (id),
    CONSTRAINT requests_users_id_fk FOREIGN KEY (requestor_id) REFERENCES users (id)
);
