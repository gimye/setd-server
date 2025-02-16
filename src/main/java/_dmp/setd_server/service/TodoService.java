package _dmp.setd_server.service;

import _dmp.setd_server.dto.request.TodoRequest;
import _dmp.setd_server.dto.response.TodoResponse;
import _dmp.setd_server.dto.request.TodoUpdateRequest;
import _dmp.setd_server.entity.Todo;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.repository.TodoRepository;
import _dmp.setd_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<TodoResponse> createTodo(String username, TodoRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<LocalDate> dates = calculateRecurringDates(
                request.getStartDate(),
                request.getEndDate(),
                request.getRepeatDays()
        );

        List<Todo> todos = dates.stream()
                .map(date -> {
                    Todo todo = new Todo();
                    todo.setUser(user);
                    todo.setTitle(request.getTitle());
                    todo.setDate(date);
                    todo.setOriginalStartDate(request.getStartDate());
                    todo.setOriginalEndDate(request.getEndDate());
                    todo.setRepeatDays(request.getRepeatDays());
                    return todo;
                })
                .toList();

        List<Todo> savedTodos = todoRepository.saveAll(todos);
        return savedTodos.stream()
                .map(TodoResponse::from)
                .toList();
    }

    private List<LocalDate> calculateRecurringDates(LocalDate start, LocalDate end, String repeatDays) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start;

        while (!current.isAfter(end)) {
            if (shouldIncludeDate(current, repeatDays, start)) {
                dates.add(current);
            }
            current = current.plusDays(1);
        }

        return dates;
    }

    private boolean shouldIncludeDate(LocalDate date, String repeatDays, LocalDate start) {
        if (repeatDays == null || repeatDays.isEmpty()) {
            return date.equals(start);
        }

        int dayOfWeek = date.getDayOfWeek().getValue() % 7;
        return repeatDays.contains(String.valueOf(dayOfWeek));
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
        if (request.getDate() != null) {
            todo.setDate(request.getDate());
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

//    public List<TodoResponse> getTodos(String username, LocalDate start, LocalDate end) {
//        return todoRepository.findByUserUsernameAndDateBetween(username, start, end)
//                .stream()
//                .map(TodoResponse::from)
//                .toList();
//    }

    public List<LocalDate> getMonthlyTodoExistence(String username, int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return todoRepository.findDistinctDatesByUserUsernameAndDateBetween(username, startOfMonth, endOfMonth);
    }

    public List<TodoResponse> getDailyTodos(String username, LocalDate date) {
        return todoRepository.findByUserUsernameAndDate(username, date)
                .stream()
                .map(TodoResponse::from)
                .toList();
    }
}
