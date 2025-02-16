package _dmp.setd_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TodoRequest {
    @NotBlank
    private String title;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private String repeatDays;
}
