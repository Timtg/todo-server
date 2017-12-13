package timgu.todoApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timgu.todoApp.domain.TodoElement;
import timgu.todoApp.service.TodoService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    List<TodoElement> getAllTodos(){
        return todoService.getAllTodos();
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity addNewTodo(@RequestBody TodoElement todoElement){
        todoService.addNewTodo(todoElement);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{todoId}")
    ResponseEntity removeTodo(@PathVariable Long todoId){
        todoService.removeTodo(todoId);
        return ResponseEntity.noContent().build();
    }


}
