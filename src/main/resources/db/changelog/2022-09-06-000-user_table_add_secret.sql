--liquibase formatted sql;
--changeset ivanov:user_table_add_secret;

alter table user
    add secret varchar(250) not null;