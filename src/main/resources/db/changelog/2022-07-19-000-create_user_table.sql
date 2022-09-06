CREATE TABLE IF NOT EXISTS user
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name    varchar(250),
    second_name varchar(250),
    email   varchar(250),
    password varchar(250),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);