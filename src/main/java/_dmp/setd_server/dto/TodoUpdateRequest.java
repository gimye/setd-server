package _dmp.setd_server.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TodoUpdateRequest {
    private String title;
    private LocalDate date;
}
