package _dmp.setd_server.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
