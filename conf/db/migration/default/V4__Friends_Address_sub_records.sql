-- Adding friends record
INSERT INTO friends
(user_id, friend_user_id)
VALUES
  ('usid_1000000', 'usid_1000001'),
  ('usid_1000000', 'usid_1000002'),
  ('usid_1000000', 'usid_1000005'),
  ('usid_1000000', 'usid_1000006'),
  ('usid_1000000', 'usid_1000008'),
  ('usid_1000000', 'usid_1000009'),
  ('usid_1000001', 'usid_1000000'),
  ('usid_1000001', 'usid_1000007'),
  ('usid_1000001', 'usid_1000009'),
  ('usid_1000001', 'usid_1000004'),
  ('usid_1000002', 'usid_1000000'),
  ('usid_1000002', 'usid_1000009');

-- Adding addresses
INSERT INTO addresses
(address_1, address_2, city, state_provience, postal_code, country)
VALUES
  ('530 Union St', NULL, 'San Francisco', 'CA', 94103, 'US'),
  ('609 Market St', NULL, 'San Francisco', 'CA', 94105, 'US'),
  ('5454 Mission St', NULL, 'San Francisco', 'CA', 94112, 'US'),
  ('24301 Southland Dr', NULL, 'Hayward', 'CA', 94545, 'US'),
  ('30600 Dyer St', NULL, 'Union City', 'CA', 94587, 'US'),
  ('County Hall', 'Westminster Bridge Rd', 'Lambeth', 'London', 7477, 'UK'),
  ('144 Praed St', NULL, 'London', 'London', 6233, 'UK'),
  ('530 Bush St', NULL, 'San Francisco', 'CA', 3345, 'UK');

-- Add users_address table
CREATE SEQUENCE public.user_address_id_seq START 1000000 CACHE 10;

CREATE TABLE public.user_addresses
(
  user_address_id VARCHAR(25)                            NOT NULL DEFAULT 'uaid_' || nextval(
      'user_address_id_seq' :: REGCLASS) :: VARCHAR,
  tag_name        VARCHAR(255),
  description     TEXT,
  is_primary      TEXT,
  user_id         VARCHAR(25)                            NOT NULL,
  address_id      VARCHAR(25)                            NOT NULL,
  created         TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  update          TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  soft_deleted    TIMESTAMP WITH TIME ZONE,
  CONSTRAINT user_addresses_user_address_id_pkey PRIMARY KEY (user_address_id),
  CONSTRAINT user_addresses_users__user_id_fk FOREIGN KEY (user_id)
  REFERENCES public.users (user_id) MATCH SIMPLE
  ON UPDATE CASCADE
  ON DELETE CASCADE,
  CONSTRAINT user_addresses_addresses__address_id_fk FOREIGN KEY (address_id)
  REFERENCES public.addresses (address_id) MATCH SIMPLE
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

-- Adding user address record
INSERT INTO user_addresses
(tag_name, description, is_primary, user_id, address_id)
VALUES
  ('Home', 'My House', 1, 'usid_1000000', 'adid_1000000'),
  ('Work', 'Where I work', 1, 'usid_1000000', 'adid_1000001'),
  ('Grocery', 'Shopping', 1, 'usid_1000000', 'adid_1000002'),
  ('Shopping', 'Fav place', 1, 'usid_1000001', 'adid_1000004'),
  ('Play', 'Game Center', 1, 'usid_1000001', 'adid_1000002'),
  ('Walmart', NULL, 1, 'usid_1000002', 'adid_1000004'),
  ('Bart', 'Train station', 1, 'usid_1000003', 'adid_1000005'),
  ('Train', 'Near home', 1, 'usid_1000004', 'adid_1000006'),
  ('Fun', 'Kids place', 1, 'usid_1000005', 'adid_1000007'),
  ('Fly', 'Gliding', 1, 'usid_1000006', 'adid_1000008'),
  ('House', 'Friends house', 1, 'usid_1000007', 'adid_1000005'),
  ('Farm', 'Work', 1, 'usid_1000008', 'adid_1000004');