-- Заполнение таблицы Account
INSERT INTO account (account_id, username, name, surname, email, moderated)
VALUES ('3baa3603-5f1d-458f-8f5b-d0509cc9f6a7','user1', 'John', 'Doe', 'john.doe@example.com', true),
       ('1e825e74-e60b-4244-bac7-cc25f3a7c7d4','user2', 'Jane', 'Smith', 'jane.smith@example.com', true),
       ('394d83ef-480d-44d4-947c-5194c9e53b6b','user3', 'Alice', 'Johnson', 'alice.johnson@example.com', true),
       ('48db2be7-297a-44ff-9a0b-ecdf60f1825e','user4', 'Bob', 'Brown', 'bob.brown@example.com', true),
       ('1349e581-1708-4290-a36a-df2363f575a3','user5', 'Charlie', 'Davis', 'charlie.davis@example.com', true),
       ('8d582ea3-3275-4f86-9658-4b74c0c9e898','user6', 'Rau', 'Davis', 'rau.davis@example.com', false);

-- Заполнение таблицы Item
INSERT INTO item (item_id, name, description, owner_id, status, moderated)
VALUES ('baef6ba1-dc19-442e-a681-151c486190a4','Item1', 'Description1', '3baa3603-5f1d-458f-8f5b-d0509cc9f6a7', 'AVAILABLE', true),
       ('5bbf3def-9503-41c9-8a04-b9420bccf3da','Item2', 'Description2', '1e825e74-e60b-4244-bac7-cc25f3a7c7d4', 'AVAILABLE', true),
       ('bd1c8579-7292-4099-892a-d75efd6164bf','Item3', 'Description3', '1e825e74-e60b-4244-bac7-cc25f3a7c7d4', 'AVAILABLE', true),
       ('749cc399-524a-4018-aab3-5db347e3976c','Item4', 'Description4', '394d83ef-480d-44d4-947c-5194c9e53b6b', 'BOOKED', true),
       ('38345a1e-9ddf-48d9-b6dd-d6e78a798df2','Item5', 'Description5', '394d83ef-480d-44d4-947c-5194c9e53b6b', 'BOOKED', true),
       ('85979ab8-c40e-4edb-bdd0-87c0bf90e905','Item6', 'Description6', '394d83ef-480d-44d4-947c-5194c9e53b6b', 'BOOKED', true),
       ('3a2f77d8-f5a4-48bb-86ba-67024e8bc0b7','Item7', 'Description7', '48db2be7-297a-44ff-9a0b-ecdf60f1825e', 'AVAILABLE', true),
       ('c27a5d4a-d1d3-4759-9249-a91049949cd9','Item8', 'Description8', '48db2be7-297a-44ff-9a0b-ecdf60f1825e', 'DISABLED', false),
       ('e747088e-b47a-4ef3-8351-fe8115304a31','Item9', 'Description9', '1349e581-1708-4290-a36a-df2363f575a3', 'AVAILABLE', true),
       ('16a79877-1665-4cbc-a2ac-69500c30ccac','Item10', 'Description10', '1349e581-1708-4290-a36a-df2363f575a3', 'AVAILABLE', false);

-- Заполнение таблицы Booking
INSERT INTO booking (booking_id, renter_id, start_date, end_date, status, description)
VALUES ('1d70f3ff-9d83-4641-844f-0c85b9a7fb2e','1e825e74-e60b-4244-bac7-cc25f3a7c7d4', '2023-1-01 10:00:00', '2023-8-10 10:00:00', 'CLOSE', 'Booking1'),
       ('53c7b205-c4f4-495e-83bd-9a4b1269785f','394d83ef-480d-44d4-947c-5194c9e53b6b', '2023-10-01 10:00:00', '2023-10-04 11:00:00', 'CANCELED', 'Booking2'),
       ('e9bfef4f-0082-428e-86c2-97f114893ec9','48db2be7-297a-44ff-9a0b-ecdf60f1825e', '2023-10-08 10:00:00', '2024-10-10 10:00:00', 'OPEN', 'Booking3');


INSERT INTO booked_items (item_id, booking_id)
VALUES ('baef6ba1-dc19-442e-a681-151c486190a4', '1d70f3ff-9d83-4641-844f-0c85b9a7fb2e'),
       ('5bbf3def-9503-41c9-8a04-b9420bccf3da', '53c7b205-c4f4-495e-83bd-9a4b1269785f'),
       ('bd1c8579-7292-4099-892a-d75efd6164bf', '53c7b205-c4f4-495e-83bd-9a4b1269785f'),
       ('749cc399-524a-4018-aab3-5db347e3976c', 'e9bfef4f-0082-428e-86c2-97f114893ec9'),
       ('38345a1e-9ddf-48d9-b6dd-d6e78a798df2', 'e9bfef4f-0082-428e-86c2-97f114893ec9'),
       ('85979ab8-c40e-4edb-bdd0-87c0bf90e905', 'e9bfef4f-0082-428e-86c2-97f114893ec9');

-- Заполнение таблицы Feedback
INSERT INTO feedback (item_id, booking_id, title, description, date, rate, moderated)
VALUES ('baef6ba1-dc19-442e-a681-151c486190a4', '1d70f3ff-9d83-4641-844f-0c85b9a7fb2e', 'Feedback1', 'Description1', '2023-8-10 12:00:00', 5, true),
       ('5bbf3def-9503-41c9-8a04-b9420bccf3da', '53c7b205-c4f4-495e-83bd-9a4b1269785f', 'Feedback2', 'Description2', '2023-10-04 11:00:00', 1, true),
       ('bd1c8579-7292-4099-892a-d75efd6164bf', '53c7b205-c4f4-495e-83bd-9a4b1269785f', 'Feedback4', 'Description4', '2023-10-04 11:00:00', 1, true),
       ('38345a1e-9ddf-48d9-b6dd-d6e78a798df2', 'e9bfef4f-0082-428e-86c2-97f114893ec9', 'Feedback3', 'Description3', '2023-10-05 11:00:00', 2, false),
       ('85979ab8-c40e-4edb-bdd0-87c0bf90e905', 'e9bfef4f-0082-428e-86c2-97f114893ec9', 'Feedback3', 'Description3', '2023-10-05 11:00:00', 2, false);

-- Заполнение таблицы WishlistItem
INSERT INTO wishlist_item (wishlist_id, wishlist_owner, title, description, found_item, status, moderated)
VALUES ('ae0e9479-78c1-4694-bd70-636dea818266','1e825e74-e60b-4244-bac7-cc25f3a7c7d4', 'Wishlist1', 'Description1', 'baef6ba1-dc19-442e-a681-151c486190a4', 'BOOKED', true),
       ('1e4e60cf-dedd-4c61-ae1c-e2c8bd18b3ca','1e825e74-e60b-4244-bac7-cc25f3a7c7d4', 'Wishlist1', 'Description1', 'baef6ba1-dc19-442e-a681-151c486190a4', 'BOOKED', true),
       ('31223b5a-68d6-45cc-a383-18f958158b3a','1349e581-1708-4290-a36a-df2363f575a3', 'Wishlist2', 'Description2', null, 'OPEN', true),
       ('3a2f77d8-f5a4-48bb-86ba-67024e8bc0b7','48db2be7-297a-44ff-9a0b-ecdf60f1825e', 'Wishlist2', 'Description2', null, 'OPEN', false),
       ('3a2f77d8-f5a4-48bb-86ba-67023e8bc0b7','394d83ef-480d-44d4-947c-5194c9e53b6b', 'Wishlist2', 'Description2', null, 'OPEN', false);

INSERT INTO wishlist_suggestions (item_id, wishlist_id)
VALUES ('baef6ba1-dc19-442e-a681-151c486190a4', 'ae0e9479-78c1-4694-bd70-636dea818266'),
       ('749cc399-524a-4018-aab3-5db347e3976c', 'ae0e9479-78c1-4694-bd70-636dea818266'),
       ('5bbf3def-9503-41c9-8a04-b9420bccf3da', '31223b5a-68d6-45cc-a383-18f958158b3a');