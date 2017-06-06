CREATE TABLE public.genders
(
  gender_id    CHARACTER VARYING(15) COLLATE pg_catalog."default" NOT NULL,
  gender_name  CHARACTER VARYING(25) COLLATE pg_catalog."default" NOT NULL,
  created      TIMESTAMP WITH TIME ZONE                           NOT NULL DEFAULT now(),
  updated      TIMESTAMP WITH TIME ZONE                           NOT NULL DEFAULT now(),
  soft_deleted TIMESTAMP WITH TIME ZONE,
  ord          SMALLINT                                           NOT NULL,
  CONSTRAINT genders_pkey PRIMARY KEY (gender_id)
)
WITH (
OIDS = FALSE
)
TABLESPACE pg_default;

CREATE UNIQUE INDEX genders_gender_id_uindex
  ON public.genders USING BTREE
  (gender_id COLLATE pg_catalog."default")
TABLESPACE pg_default;

CREATE UNIQUE INDEX genders_gender_name_uindex
  ON public.genders USING BTREE
  (gender_name COLLATE pg_catalog."default")
TABLESPACE pg_default;

CREATE SEQUENCE public.users_id_seq START 1000000 CACHE 10;

CREATE TABLE public.users
(
  user_id       VARCHAR(25)                                         NOT NULL DEFAULT 'atet_' || nextval(
      'users_id_seq' :: REGCLASS) :: VARCHAR,
  first_name    CHARACTER VARYING(100) COLLATE pg_catalog."default" NOT NULL,
  last_name     CHARACTER VARYING(100) COLLATE pg_catalog."default" NOT NULL,
  email         CHARACTER VARYING(255) COLLATE pg_catalog."default" NOT NULL,
  mobile_number CHARACTER VARYING(20) COLLATE pg_catalog."default",
  image         CHARACTER VARYING(255) COLLATE pg_catalog."default" NOT NULL,
  password      CHARACTER VARYING(255) COLLATE pg_catalog."default",
  salt          CHARACTER VARYING(255) COLLATE pg_catalog."default",
  gender_id     CHARACTER VARYING(15) COLLATE pg_catalog."default",
  created       TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT now(),
  updated       TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT now(),
  soft_deleted  TIMESTAMP WITH TIME ZONE,
  CONSTRAINT users_pkey PRIMARY KEY (user_id),
  CONSTRAINT users_genders_gender_id_fk FOREIGN KEY (gender_id)
  REFERENCES public.genders (gender_id) MATCH SIMPLE
  ON UPDATE CASCADE
  ON DELETE NO ACTION
)
WITH (
OIDS = FALSE
)
TABLESPACE pg_default;