--liquibase formatted sql;
--changeset ivanov:user_create_security_question_table;

CREATE TABLE IF NOT EXISTS security_question
(
    id bigint AUTO_INCREMENT PRIMARY KEY,
    user_id    bigint,
    security_question_id bigint,
    answer    varchar(250),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (security_question_id) REFERENCES security_question_definition(id));