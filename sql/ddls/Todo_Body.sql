CREATE TABLE public.Todo_Body
(
    id SERIAL PRIMARY KEY NOT NULL,
    Created_Date TIMESTAMP NOT NULL,
    Deleted BOOLEAN NOT NULL,
    Body TEXT NOT NULL,
);
CREATE UNIQUE INDEX Todo_Body_id_uindex ON public.Todo_Body (id);