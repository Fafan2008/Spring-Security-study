CREATE TABLE IF NOT EXISTS password_reset_token
(
    id bigint AUTO_INCREMENT PRIMARY KEY,
    token    varchar(250),
    user_id bigint,
    expiry_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id));