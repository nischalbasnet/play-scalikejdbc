-- Adding friends record
INSERT INTO friends
(user_id, friend_user_id)
VALUES
  ('usid_1000010', 'usid_1000011'),
  ('usid_1000010', 'usid_1000012'),
  ('usid_1000010', 'usid_1000015'),
  ('usid_1000010', 'usid_1000016'),
  ('usid_1000010', 'usid_1000018'),
  ('usid_1000010', 'usid_1000019'),
  ('usid_1000011', 'usid_1000010'),
  ('usid_1000011', 'usid_1000017'),
  ('usid_1000011', 'usid_1000019'),
  ('usid_1000011', 'usid_1000014'),
  ('usid_1000012', 'usid_1000010'),
  ('usid_1000012', 'usid_1000019');

-- Modify Address table to add county
ALTER TABLE public.addresses
  ADD country VARCHAR(255) NOT NULL;

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
