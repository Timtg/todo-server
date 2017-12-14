package timgu.todoApp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import timgu.todoApp.domain.Category;
import timgu.todoApp.domain.TodoElement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TodoJdbcDao implements TodoDao {

    private final JdbcTemplate template;
    private final SimpleJdbcInsert todoInsertActor;


    @Autowired
    public TodoJdbcDao(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
        todoInsertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("todo_body")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public List<TodoElement> getAllTodos() {
        String sql = "SELECT tb.id, tb.created_date todo_created, tb.body,c.id category_id, c.description " +
                "FROM Todo_Body as tb " +
                "LEFT JOIN todo_element_category as tc ON tb.id = tc.todo_element_id " +
                "LEFT JOIN category as c on tc.category_id = c.id " +
                "where tb.deleted = FALSE";

        List<TodoElement> todoElements = new ArrayList<>();
        List<TodoElement> todoElementList = template.query(sql, (rs, rowNum) -> mapTodo(rs, todoElements));
        HashSet<Object> seen = new HashSet<>();
        todoElementList.removeIf(todoElement -> !seen.add(todoElement.getId()));
        return todoElementList;

    }

    @Override
    public void removeTodoById(Long todoId) {
        String sql = "UPDATE todo_body " +
                "SET deleted = TRUE " +
                "WHERE id = ?";
        template.update(sql, todoId);
    }

    @Override
    public boolean getDuplicateTodoElements(TodoElement todoElement) {

        StringJoiner joiner = new StringJoiner(",", "(", ")");
        String categorySql = "";
        if(todoElement.getCategories() != null && !todoElement.getCategories().isEmpty()) {
            todoElement.getCategories().forEach(category -> joiner.add("" + category.getCategoryId()));
            categorySql = " and category_id in " + joiner.toString();
        }
        String sql = "SELECT count (tb.id) as count " +
                "FROM todo_body tb, todo_element_category tc, category c where tb.body =" +
                " ? AND tb.id = tc.todo_element_id and tc.category_id = c.id" + categorySql;

        return template.queryForObject(sql, (rs, rowNum) -> rs.getLong("count"), todoElement.getBody()) > 0;

    }

    @Override
    public Long insertTodoElement(TodoElement todoElement) {
        Map<String, Object> insertParams = new HashMap<>();
        insertParams.put("body", todoElement.getBody());
        insertParams.put("Created_date", Timestamp.valueOf(LocalDateTime.now()));
        insertParams.put("deleted", false);

        return todoInsertActor.executeAndReturnKey(insertParams).longValue();
    }

    @Override
    public void insertCategoryLink(Long todoId, List<Category> categories) {
        StringJoiner joiner = new StringJoiner(",", "(", ")");
        categories.forEach(category -> joiner.add(todoId + "," + category.getCategoryId()));

        String sql ="INSERT INTO todo_element_category (todo_element_id, category_id) VALUES ?";
        template.update(sql, joiner.toString());
    }

    private TodoElement mapTodo(ResultSet rs, List<TodoElement> todoElements) throws SQLException {
        Long id = rs.getLong("Id");
        List<Long> todoElementIds = todoElements.stream().map(TodoElement::getId).collect(Collectors.toList());
        if (!todoElementIds.contains(id)) {
            todoElementIds.add(id);
            TodoElement todoElement = new TodoElement();
            todoElement.setId(id);
            todoElement.setBody(rs.getString("body"));
            todoElement.setCreatedTime(LocalDateTime.ofInstant(rs.getTimestamp("todo_created").toInstant(), ZoneId.systemDefault()));
            Category category = new Category();
            category.setCategoryId(rs.getLong("category_id"));
            category.setCategoryName(rs.getString("description"));
            ArrayList<Category> categories = new ArrayList<>();
            categories.add(category);
            todoElement.setCategories(categories);
            todoElements.add(todoElement);
            return todoElement;
        } else {
            for (int i = 0; i < todoElements.size(); i++) {
                TodoElement todoElement = todoElements.get(i);
                if (todoElement.getId().equals(id)) {
                    List<Category> categories = todoElement.getCategories();
                    Category category = new Category();
                    category.setCategoryId(rs.getLong("category_id"));
                    category.setCategoryName(rs.getString("description"));
                    categories.add(category);
                    todoElement.setCategories(categories);
                    todoElements.set(i, todoElement);
                    return todoElement;
                }
            }
        }
        return null;
    }
}
