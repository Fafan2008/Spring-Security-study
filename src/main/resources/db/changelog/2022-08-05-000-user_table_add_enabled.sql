--liquibase formatted sql;
--changeset ivanov:user_table_add_enabled;

alter table user
    add enabled bool default false;