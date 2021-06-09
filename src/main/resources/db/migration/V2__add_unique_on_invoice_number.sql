ALTER TABLE public.invoice
    ADD CONSTRAINT unique_invoices_number UNIQUE (number);