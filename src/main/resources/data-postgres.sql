INSERT INTO ocsp_cert(id, file_name, revoked, issuer_id) VALUES (1, 'root', false, null);
INSERT INTO ocsp_cert(id, file_name, revoked, issuer_id) VALUES (2, 'intermediate', false, 1);
INSERT INTO ocsp_cert(id, file_name, revoked, issuer_id) VALUES (3, 'end', false, 2);

INSERT INTO AUTHORITY (id ,name) VALUES (1, 'ROLE_ADMINISTRATOR');
INSERT INTO AUTHORITY (id ,name) VALUES (2, 'ROLE_INSTAGRAM_USER');
INSERT INTO AUTHORITY (id ,name) VALUES (3, 'ROLE_AGENT');

INSERT INTO PRIVILEGE (id ,name) VALUES (1, 'CERTIFICATE');
INSERT INTO PRIVILEGE (id ,name) VALUES (2, 'NISTAGRAM_USER_PRIVILEGE');
INSERT INTO PRIVILEGE (id ,name) VALUES (3, 'FOLLOW_PRIVILEGE');
INSERT INTO PRIVILEGE (id ,name) VALUES (4, 'CAMPAIGN_PRIVILEGE');

INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (1, 1);
INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (2, 2);
INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (2, 3);
INSERT INTO ROLES_PRIVILEGES (role_id, privilege_id) VALUES (3, 4);

INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username) VALUES (1, 'ADMINISTRATOR', 'Admin', 'Admin', 'pharmacyisa6+admin@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', true, 0, 0, 'admin');

INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username, about, date_of_birth, geneder, phone_number, registration_sent_date, profile_private, messages_enabled, tags_enabled, verification_status, agent) VALUES (2, 'INSTAGRAM_USER', 'Luka', 'Doric', 'pharmacyisa6+luka@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', true, 0, 0,
 'luka', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', '1998-06-16 02:00:00', 0, '123', '2021-05-21 15:39:47.739', true, true, true, 1, false);
 INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username, about, profile_private, messages_enabled, tags_enabled, date_of_birth, geneder, agent) VALUES (3, 'INSTAGRAM_USER', 'Vlado', 'Budjen', 'pharmacyisa6+vlada@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', true, 0, 0,
 'vlado', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', false, true, true, '2000-05-09 02:00:00', 0, false);
 INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username, about, profile_private, messages_enabled, tags_enabled, date_of_birth, geneder, agent) VALUES (4, 'INSTAGRAM_USER', 'Makro', 'Weedovic', 'pharmacyisa6+vidoje@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', true, 0, 0,
 'vidoje', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', true, true, true, '1995-02-16 02:00:00', 0, false);
 INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username, about, profile_private, messages_enabled, tags_enabled, date_of_birth, geneder, agent) VALUES (5, 'INSTAGRAM_USER', 'Milica', 'Siriski', 'pharmacyisa6+milica@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', true, 0, 0,
 'milica', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', true, true, true, '1998-07-07 02:00:00', 1, false);
  INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username, about, profile_private, messages_enabled, tags_enabled, date_of_birth, geneder, agent) VALUES (6, 'INSTAGRAM_USER', 'Dusan', 'Damnjanovic', 'pharmacyisa6+duja@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', true, 0, 0,
 'duja', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', false, true, true, '1970-06-25 02:00:00', 0, false);
  INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username, about, profile_private, messages_enabled, tags_enabled, date_of_birth, geneder, agent) VALUES (7, 'INSTAGRAM_USER', 'Kobra', 'Kobra', 'pharmacyisa6+kobra@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', true, 0, 0,
 'kobra', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', true, true, false, '1933-05-20 02:00:00', 1, false);
 INSERT INTO gram_user (id, user_type , name, surname, email, password, last_password_reset_date, enabled, password_reset_failed, two_factor_auth_count, nistagram_username, about, profile_private, messages_enabled, tags_enabled, date_of_birth, geneder, agent, website) VALUES (8, 'INSTAGRAM_USER', 'Milica', 'Jaric', 'pharmacyisa6+jara@gmail.com', '$2a$10$/b6nrwDAIMHQv/wAeD004u91l/k.973ksiTVbL1yJCKw3TuVaHMf6', '2017-10-01 21:58:58.508-07', false, 0, 0,
 'jara', 'Vegan friendly and cruelty free make-up and cosmetics', true, true, true, '1998-06-20 02:00:00', 1, true, 'www.jaracosmetics.com');

INSERT INTO public.verification_request(
	id, approved, category, official_document_image_name, resolved, user_id)
	VALUES (1, true, 0, 'image1.jpg', true, 3);

INSERT INTO public.verification_request(
	id, approved, category, official_document_image_name, resolved, user_id)
	VALUES (2, true , 0, 'image2.jpg', true, 4);

INSERT INTO public.verification_request(
	id, approved, category, official_document_image_name, resolved, user_id)
	VALUES (3, true , 0, 'image3.jpg', true, 5);

INSERT INTO public.verification_request(
	id, approved, category, official_document_image_name, resolved, user_id)
	VALUES (4, true , 0, 'image3.jpg', true, 6);

INSERT INTO public.verification_request(
	id, approved, category, official_document_image_name, resolved, user_id)
	VALUES (5, true , 0, 'image3.jpg', true, 7);

INSERT INTO user_authority(user_id, authority_id) VALUES (1, 1);
INSERT INTO user_authority(user_id, authority_id) VALUES (2, 2);
INSERT INTO user_authority(user_id, authority_id) VALUES (3, 2);
INSERT INTO user_authority(user_id, authority_id) VALUES (4, 2);
INSERT INTO user_authority(user_id, authority_id) VALUES (5, 2);
INSERT INTO user_authority(user_id, authority_id) VALUES (6, 2);
INSERT INTO user_authority(user_id, authority_id) VALUES (7, 2);
INSERT INTO user_authority(user_id, authority_id) VALUES (8, 2);
INSERT INTO user_authority(user_id, authority_id) VALUES (8, 3);