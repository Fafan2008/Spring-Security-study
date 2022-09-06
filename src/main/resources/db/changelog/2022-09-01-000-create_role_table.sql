--liquibase formatted sql;
--changeset ivanov:create_role_table;

CREATE TABLE IF NOT EXISTS role
(
    id    bigint AUTO_INCREMENT PRIMARY KEY,
    name  varchar(150) not null
);

CREATE TABLE IF NOT EXISTS privilege
(
    id    bigint AUTO_INCREMENT PRIMARY KEY,
    name  varchar(150) not null
);

CREATE TABLE IF NOT EXISTS roles_privileges
(
    role_id    bigint not null,
    privilege_id  bigint not null,
    PRIMARY KEY (role_id, privilege_id),
    CONSTRAINT fk_role_roles_privileges FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_privilege_roles_privileges FOREIGN KEY (privilege_id) REFERENCES privilege (id)
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id    bigint not null,
    role_id  bigint not null,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_role_users_roles FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_user_users_roles FOREIGN KEY (user_id) REFERENCES user (id)
);