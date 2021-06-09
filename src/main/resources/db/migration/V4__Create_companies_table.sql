CREATE TABLE public.company
(
    id bigserial NOT NULL,
    tax_identification_number character varying(50) NOT NULL,
    address character varying(200) NOT NULL,
    name character varying(100) NOT NULL,
    pension_insurance numeric(10, 2) NOT NULL,
    health_insurance numeric(10, 2) NOT NULL,
    PRIMARY KEY (id)
);
