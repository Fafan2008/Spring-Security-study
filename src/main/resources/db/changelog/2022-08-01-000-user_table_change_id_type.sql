--liquibase formatted sql;
--changeset ivanov:user_table_change_id_type;

ALTER TABLE user MODIFY COLUMN id bigint auto_increment;