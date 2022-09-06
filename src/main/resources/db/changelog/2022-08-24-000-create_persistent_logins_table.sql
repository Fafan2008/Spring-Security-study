--liquibase formatted sql;
--changeset ivanov:user_create_persistent_logins_table;

CREATE TABLE IF NOT EXISTS persistent_logins
(
    series    varchar(64) PRIMARY KEY,
    username  varchar(64) not null,
    token     varchar(64) not null,
    last_used timestamp   not null
);