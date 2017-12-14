CREATE TABLE public.category
(
    id SERIAL PRIMARY KEY NOT NULL,
    Description TEXT NOT NULL,
    Deleted BOOLEAN NOT NULL,
    Created_Date TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX Categories_id_uindex ON public.category (id);