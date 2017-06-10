-- Creating friends table
CREATE SEQUENCE public.friends_id_seq START 1000000 CACHE 10;

CREATE TABLE public.friends
(
  friend_id      VARCHAR(25)                            NOT NULL DEFAULT 'frid_' || nextval(
      'friends_id_seq' :: REGCLASS) :: VARCHAR,
  user_id        VARCHAR(25)                            NOT NULL,
  friend_user_id VARCHAR(25)                            NOT NULL,
  created        TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  update         TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  soft_deleted   TIMESTAMP WITH TIME ZONE,
  CONSTRAINT friends_friend_id_pk PRIMARY KEY (friend_id)
);

CREATE SEQUENCE public.addresses_id_seq START 1000000 CACHE 10;

CREATE TABLE public.addresses
(
  address_id      VARCHAR(25)                            NOT NULL DEFAULT 'adid_' || nextval(
      'addresses_id_seq' :: REGCLASS) :: VARCHAR,
  address_1       TEXT                                   NOT NULL,
  address_2       TEXT,
  city            VARCHAR(255),
  state_provience VARCHAR(255),
  postal_code     INT,
  created         TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  updated         TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  soft_deleted    TIMESTAMP WITH TIME ZONE,
  CONSTRAINT addresses_address_id_pk PRIMARY KEY (address_id)
);
