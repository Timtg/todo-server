CREATE TABLE public.Todo_element_category
(
    todo_element_id INT NOT NULL,
    category_id INT NOT NULL,
    CONSTRAINT Todo_element_category_todo_body_id_fk FOREIGN KEY (todo_element_id) REFERENCES todo_body (id),
    CONSTRAINT Todo_element_category_category_id_fk FOREIGN KEY (category_id) REFERENCES category (id)
);