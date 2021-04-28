-- INSERT INTO ocsp_cert(id, file_name, is_revoked, issuer_id) VALUES ('100', 'root', false, null);
-- INSERT INTO ocsp_cert(id, file_name, is_revoked, issuer_id) VALUES ('101', 'intermediate', false, 100);
-- INSERT INTO ocsp_cert(id, file_name, is_revoked, issuer_id) VALUES ('102', 'endEntity', false, 101);

INSERT INTO AUTHORITY (id ,name) VALUES (1, 'ROLE_ADMINISTRATOR');
INSERT INTO AUTHORITY (id ,name) VALUES (2, 'ROLE_INSTAGRAM_USER');

INSERT INTO PRIVILEGE (id ,name) VALUES (1, 'CERTIFICATE');
INSERT INTO PRIVILEGE (id ,name) VALUES (2, 'TEST');

INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (1, 1);
INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (2, 2);

INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (2, 2);
INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed) VALUES (1, 'ADMINISTRATOR', 'Admin', 'Admin', 'pharmacyisa6+admin@gmail.com', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', '2017-10-01 21:58:58.508-07', true, 0);