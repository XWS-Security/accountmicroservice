-- INSERT INTO ocsp_cert(id, file_name, is_revoked, issuer_id) VALUES ('100', 'root', false, null);
-- INSERT INTO ocsp_cert(id, file_name, is_revoked, issuer_id) VALUES ('101', 'intermediate', false, 100);
-- INSERT INTO ocsp_cert(id, file_name, is_revoked, issuer_id) VALUES ('102', 'endEntity', false, 101);

INSERT INTO AUTHORITY (id ,name) VALUES (1, 'ROLE_ADMINISTRATOR');
INSERT INTO AUTHORITY (id ,name) VALUES (2, 'ROLE_INSTAGRAM_USER');

INSERT INTO PRIVILEGE (id ,name) VALUES (1, 'READ');
INSERT INTO PRIVILEGE (id ,name) VALUES (2, 'WRITE');

INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (2, 1);
INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (2, 2);