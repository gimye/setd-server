package _dmp.setd_server.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import _dmp.setd_server.entity.Todo;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TodoResponse {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String repeatDays;

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getStartDate(),
                todo.getEndDate(),
                todo.getRepeatDays()
        );
    }
}
