INSERT INTO Users(name, username, passwordHash) VALUES ('Integration test admin', 'itadmin','$2a$10$CUmO0bpNJ16FagDj7RMDTOR3nvmojh0RmGtOQ7Bar83dKE9kRp0AW');
INSERT INTO UserRoles (userId, roleId) VALUES(2,1);
INSERT INTO UserRoles (userId, roleId) VALUES(2,2);