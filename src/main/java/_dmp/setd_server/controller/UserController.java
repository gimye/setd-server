package _dmp.setd_server.controller;

import _dmp.setd_server.dto.*;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.service.UserService;
import _dmp.setd_server.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest.getUsername(),
                    registerRequest.getPassword(), registerRequest.getNickname());
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("중복된 아이디입니다.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        User user = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        if(user!=null){
            String token = jwtUtil.generateToken(user.getUsername());
            UserResponseDTO userResponseDTO = userService.getUserResponseDTO(user);
            return  ResponseEntity.ok(new LoginResponse(token, userResponseDTO));
        }
        return ResponseEntity.badRequest().body("Invalid username or password");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}