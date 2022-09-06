--liquibase formatted sql;
--changeset ivanov:email_uniq;

ALTER TABLE user
    ADD UNIQUE (email);