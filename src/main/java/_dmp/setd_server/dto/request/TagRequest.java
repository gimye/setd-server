package _dmp.setd_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
