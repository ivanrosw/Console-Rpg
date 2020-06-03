CREATE TABLE registered_users(
id serial PRIMARY KEY,
username VARCHAR(50) NOT NULL UNIQUE ,
password VARCHAR(200) NOT NULL
);

CREATE TABLE users_characters(
id serial PRIMARY KEY,
user_id bigint REFERENCES registered_users(id) ON UPDATE CASCADE ON DELETE CASCADE,
name text NOT NULL,
level bigint DEFAULT 1,
strength bigint DEFAULT 1,
agility bigint DEFAULT 1,
intelligence bigint DEFAULT 1,
hero_class VARCHAR(20),
enemies_kill bigint DEFAULT 0,
quests_done bigint DEFAULT 0,
game_count int DEFAULT 1,
gold int DEFAULT 0
);

CREATE TABLE characters_equipments(
id serial PRIMARY KEY,
character_id bigint REFERENCES users_characters(id) ON UPDATE CASCADE ON DELETE CASCADE,
name VARCHAR(50) NOT NULL,
hero_class VARCHAR(50) NOT NULL,
body_part VARCHAR(50) NOT NULL,
level int ,
strength int,
agility int,
intelligence int,
gold int DEFAULT 0
);

CREATE TABLE characters_bag_equipments(
id serial PRIMARY KEY,
character_id bigint REFERENCES users_characters(id) ON UPDATE CASCADE ON DELETE CASCADE,
name VARCHAR(50) NOT NULL,
hero_class VARCHAR(50) NOT NULL,
body_part VARCHAR(50) NOT NULL,
level int ,
strength int,
agility int,
intelligence int,
gold int DEFAULT 0
);

CREATE TABLE characters_bag_usable(
id serial PRIMARY KEY,
character_id bigint REFERENCES users_characters(id) ON UPDATE CASCADE ON DELETE CASCADE,
name VARCHAR(50) NOT NULL,
count int,
gold int DEFAULT 0
);
