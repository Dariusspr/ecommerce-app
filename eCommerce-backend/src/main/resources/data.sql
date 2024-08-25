-- ROLES
INSERT
IGNORE INTO role
VALUES (1, 'MEMBER'),
       (2, 'ADMIN');

-- MEMBERS
INSERT
IGNORE INTO member
VALUES (1, NOW(), NOW(), TRUE, FALSE, 'email123@gmail.com', 'MALE', '$2a$10$gHCnBDU/DGakO4B21LblmOktASvUeupNQ/5oH0jbsigpJBhsC4UQ2', 'default-profile', 'default-member-url','PNG', 'member', 1),
 (2, NOW(), NOW(), TRUE, FALSE, 'email321@gmail.com', 'MALE', '$2a$10$qCIJt7zh/30lLic5c0jPe.s1KFHvwJuiRq4fC/frJ/ViiHxPk5Fqu', 'default-profile', 'default-member-url','PNG', 'admin', 2);
