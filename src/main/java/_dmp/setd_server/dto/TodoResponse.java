package _dmp.setd_server.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import _dmp.setd_server.entity.Todo;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
public class TodoResponse {
    private Long id;
    private String title;
    private LocalDate date;

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDate()
        );
    }
}
