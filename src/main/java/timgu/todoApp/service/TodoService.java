package timgu.todoApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import timgu.todoApp.dao.TodoDao;
import timgu.todoApp.domain.TodoElement;

import java.util.List;

@Service
public class TodoService {

    private final TodoDao todoDao;

    @Autowired
    public TodoService(TodoDao todoDao){
        this.todoDao = todoDao;
    }

    public List<TodoElement> getAllTodos() {
        return todoDao.getAllTodos();

    }

    public void addNewTodo(TodoElement todoElement) {
        if (checkForDuplicateTodoElements(todoElement)){
            throw new IllegalArgumentException("Cannot insert duplicate todo element");
        }
        Long todoId = todoDao.insertTodoElement(todoElement);
        if (todoElement.getCategories() != null) {
            todoDao.insertCategoryLink(todoId, todoElement.getCategories());
        }
    }

    private boolean checkForDuplicateTodoElements(TodoElement todoElement) {
        return todoDao.getDuplicateTodoElements(todoElement);
    }

    public void removeTodo(Long todoId) {
        todoDao.removeTodoById(todoId);
    }
}
