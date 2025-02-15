package _dmp.setd_server.service;

import _dmp.setd_server.dto.TodoRequest;
import _dmp.setd_server.dto.TodoResponse;
import _dmp.setd_server.dto.TodoUpdateRequest;
import _dmp.setd_server.entity.Todo;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.repository.TodoRepository;
import _dmp.setd_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public TodoResponse createTodo(String username, TodoRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Todo todo = new Todo();
        todo.setUser(user);
        todo.setTitle(request.getTitle());
        todo.setStartDate(request.getStartDate());
        todo.setEndDate(request.getEndDate());
        todo.setRepeatDays(request.getRepeatDays());

        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.from(savedTodo);
    }

    @Transactional
    public TodoResponse updateTodo(String username, Long todoId, TodoUpdateRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!todo.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You don't have permission to update this todo");
        }

        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getStartDate() != null) {
            todo.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            todo.setEndDate(request.getEndDate());
        }
        if (request.getRepeatDays() != null) {
            todo.setRepeatDays(request.getRepeatDays());
        }

        Todo updatedTodo = todoRepository.save(todo);
        return TodoResponse.from(updatedTodo);
    }

    @Transactional
    public void deleteTodo(String username, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!todo.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You don't have permission to delete this todo");
        }

        todoRepository.delete(todo);
    }

    public List<TodoResponse> getTodos(String username, LocalDate start, LocalDate end) {
        return todoRepository.findByUserUsernameAndStartDateBetween(username, start, end)
                .stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }
}
