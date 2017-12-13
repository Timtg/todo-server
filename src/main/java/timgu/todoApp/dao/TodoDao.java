package timgu.todoApp.dao;

import timgu.todoApp.domain.Category;
import timgu.todoApp.domain.TodoElement;

import java.util.List;

public interface TodoDao {


    List<TodoElement> getAllTodos();

    void removeTodoById(Long todoId);

    boolean getDuplicateTodoElements(TodoElement todoElement);

    Long insertTodoElement(TodoElement todoElement);

    void insertCategoryLink(Long todoId, List<Category> categories);

}
