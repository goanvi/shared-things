-- Заполнение таблицы Account
INSERT INTO account (username, name, surname, email, moderated)
VALUES ('user1', 'John', 'Doe', 'john.doe@example.com', true),
       ('user2', 'Jane', 'Smith', 'jane.smith@example.com', true),
       ('user3', 'Alice', 'Johnson', 'alice.johnson@example.com', true),
       ('user4', 'Bob', 'Brown', 'bob.brown@example.com', true),
       ('user5', 'Charlie', 'Davis', 'charlie.davis@example.com', true),
       ('user6', 'Rau', 'Davis', 'rau.davis@example.com', false);

-- Заполнение таблицы Item
INSERT INTO item (name, description, owner_id, status, moderated)
VALUES ('Item1', 'Description1', 1, 'AVAILABLE', true),
       ('Item2', 'Description2', 2, 'AVAILABLE', true),
       ('Item3', 'Description3', 2, 'AVAILABLE', true),
       ('Item4', 'Description4', 3, 'BOOKED', true),
       ('Item5', 'Description5', 3, 'BOOKED', true),
       ('Item6', 'Description6', 3, 'BOOKED', true),
       ('Item7', 'Description7', 4, 'AVAILABLE', true),
       ('Item8', 'Description8', 4, 'DISABLED', false),
       ('Item9', 'Description9', 5, 'AVAILABLE', true),
       ('Item10', 'Description10', 5, 'AVAILABLE', false);

-- Заполнение таблицы Booking
INSERT INTO booking (renter_id, start_date, end_date, status, description)
VALUES (2, '2023-1-01 10:00:00', '2023-8-10 10:00:00', 'CLOSE', 'Booking1'),
       (3, '2023-10-01 10:00:00', '2023-10-04 11:00:00', 'CANCELED', 'Booking2'),
       (4, '2023-10-08 10:00:00', '2024-10-10 10:00:00', 'OPEN', 'Booking3');


INSERT INTO booked_items (item_id, booking_id)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (4, 3),
       (5, 3),
       (6, 3);

-- Заполнение таблицы Feedback
INSERT INTO feedback (item_id, booking_id, title, description, date, rate, moderated)
VALUES (1, 1, 'Feedback1', 'Description1', '2023-8-10 12:00:00', 5, true),
       (2, 2, 'Feedback2', 'Description2', '2023-10-04 11:00:00', 1, false);

-- Заполнение таблицы WishlistItem
INSERT INTO wishlist_item (wishlist_owner, title, description, found_item, status, moderated)
VALUES (2, 'Wishlist1', 'Description1', 1, 'BOOKED', true),
       (5, 'Wishlist2', 'Description2', null, 'ACTIVE', true);

INSERT INTO wishlist_suggestions (item_id, wishlist_id)
VALUES (1, 1),
       (4, 1),
       (2, 2);