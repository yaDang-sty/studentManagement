DELETE FROM user_password WHERE id IN ('9999999', 'admin');
INSERT INTO user_password(id, password) VALUES ('9999999', '1234');
INSERT INTO user_password(id, password) VALUES ('admin', '1234');
