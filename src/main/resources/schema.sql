DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id serial primary key,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email)
);
