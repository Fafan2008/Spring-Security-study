--liquibase formatted sql;
--changeset ivanov:insert_test_data;

insert into myapp.user (id, first_name, second_name, email, password, login_name, enabled)
values (2, 'test', 'test', 'test@mail.com', '$2a$10$bQrRrsi7KrxZJPky61kF5euYOgratPGP1SqC/7Mh6CsID.bK.5CyC', 'test',
        true);

insert into role (id, name)
values (1, 'ROLE_ADMIN');

insert into role (id, name)
values (2, 'ROLE_USER');

insert into privilege (id, name)
values (1, 'READ_PRIVILEGE');

insert into privilege (id, name)
values (2, 'WRITE_PRIVILEGE');

insert into roles_privileges (role_id, privilege_id)
values (1, 1);
insert into roles_privileges (role_id, privilege_id)
values (1, 2);
insert into roles_privileges (role_id, privilege_id)
values (2, 1);

insert into users_roles (user_id, role_id)
values (2, 1);