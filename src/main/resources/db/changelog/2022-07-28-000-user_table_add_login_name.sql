--liquibase formatted sql;
--changeset ivanov:user_table_add_login_name;

alter table user
    add login_name varchar(250);

UPDATE user SET login_name=email;

alter table user
    modify login_name varchar(250) not null;

ALTER TABLE user
    ADD UNIQUE (login_name);