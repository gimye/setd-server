package _dmp.setd_server.controller;

import _dmp.setd_server.dto.*;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.service.UserService;
import _dmp.setd_server.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // 회원가입 API
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

    // Login API
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

    // 회원탈퇴 API
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    // 로그아웃은 client 단에서 구현
    // 닉네임 변경 API
    @PatchMapping("/nickname")
    public ResponseEntity<UserResponseDTO> updateNickname (
            @RequestBody NicknameUpdateRequest request) {
        return ResponseEntity.ok(userService.updateNickname(request.getUsername(), request.getNickname()));
    }
}