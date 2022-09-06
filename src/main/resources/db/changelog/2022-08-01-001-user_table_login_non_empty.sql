--liquibase formatted sql;
--changeset ivanov:non_null_user_name;

ALTER TABLE user ADD CONSTRAINT not_null_login_name CHECK (login_name <> '');