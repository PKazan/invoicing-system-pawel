CREATE TABLE public.car
(
id bigserial NOT NULL,
registration_number character varying(20) NOT NULL,
including_private_expense boolean NOT NULL DEFAULT false,
PRIMARY KEY (id)
);

