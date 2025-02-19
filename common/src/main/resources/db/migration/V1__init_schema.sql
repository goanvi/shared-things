create table account
(
    account_id UUID primary key,
    username   varchar(100) not null unique,
    name       varchar(100),
    surname    varchar(100),
    email      varchar(100) unique,
    moderated  boolean      not null default false
);

create table item
(
    item_id     UUID primary key,
    name        varchar(100)                        not null,
    description text,
    owner_id    UUID references account (account_id) not null,
    status      varchar(30)                         not null,
    moderated   boolean                             not null default false
);

create table booking
(
    booking_id  UUID primary key,
    renter_id   UUID references account (account_id) not null,
    start_date  timestamp                           not null default now(),
    end_date    timestamp                           not null,
    status      varchar(30)                         not null,
    description text
);

create table feedback
(
    item_id     UUID references item (item_id)       not null,
    booking_id  UUID references booking (booking_id) not null,
    title       varchar(300)                        not null,
    description text,
    date        timestamp                           not null default now(),
    rate        int                                 not null check ( rate between 1 and 10 ),
    moderated   boolean                             not null default false,
    primary key (item_id, booking_id)
);

create table booked_items
(
    item_id    UUID references item (item_id)       not null,
    booking_id UUID references booking (booking_id) not null,
    primary key (item_id, booking_id)
);

create table wishlist_item
(
    wishlist_id    UUID primary key,
    wishlist_owner UUID references account (account_id) not null,
    title          varchar(300)                        not null,
    description    text,
    found_item     UUID references item (item_id),
    status         varchar(30)                         not null,
    moderated      boolean                             not null default false
);

create table wishlist_suggestions
(
    item_id     UUID references item (item_id)              not null,
    wishlist_id UUID references wishlist_item (wishlist_id) not null,
    primary key (item_id, wishlist_id)
);

create table users (
    id uuid primary key ,
    username text not null unique ,
    password text not null unique ,
    role user_role not null default 'USER',
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);
