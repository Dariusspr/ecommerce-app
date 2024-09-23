-- ROLES
INSERT INTO role (role_id, role_title)
VALUES (1, 'MEMBER'),
       (2, 'ADMIN') ON DUPLICATE KEY
UPDATE role_id = role_id;

-- MEMBERS
INSERT INTO member (member_id, created_date, last_modified_date, member_enabled, member_locked, member_email,
                    member_gender, member_password, member_profile_title, member_profile_key, member_profile_url,
                    member_profile_format, member_username, member_role_id)
VALUES (1, NOW(), NOW(), TRUE, FALSE, 'test123@gmail.com', 'MALE',
        '$2a$10$gHCnBDU/DGakO4B21LblmOktASvUeupNQ/5oH0jbsigpJBhsC4UQ2', 'default-profile', 'default-profile-img-key',
        'https://upload.wikimedia.org/wikipedia/commons/a/ac/Default_pfp.jpg?20200418092106', 'JPG', 'member', 1),
       (2, NOW(), NOW(), TRUE, FALSE, 'test321@gmail.com', 'MALE',
        '$2a$10$qCIJt7zh/30lLic5c0jPe.s1KFHvwJuiRq4fC/frJ/ViiHxPk5Fqu', 'default-profile', 'default-profile-img-key',
        'https://upload.wikimedia.org/wikipedia/commons/a/ac/Default_pfp.jpg?20200418092106', 'JPG', 'admin',
        2) ON DUPLICATE KEY
UPDATE
    member_id = member_id;


