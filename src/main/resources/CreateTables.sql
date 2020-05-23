CREATE TABLE registered_users(
id serial PRIMARY KEY,
username VARCHAR(50) NOT NULL UNIQUE ,
password VARCHAR(200) NOT NULL
);