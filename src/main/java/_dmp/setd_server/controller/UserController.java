package _dmp.setd_server.controller;

import _dmp.setd_server.dto.request.LoginRequest;
import _dmp.setd_server.dto.request.NicknameUpdateRequest;
import _dmp.setd_server.dto.request.RegisterRequest;
import _dmp.setd_server.dto.response.LoginResponse;
import _dmp.setd_server.dto.response.UserResponse;
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
            return ResponseEntity.ok(user);
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
            UserResponse userResponse = userService.getUserResponseDTO(user);
            return  ResponseEntity.ok(new LoginResponse(token, userResponse));
        }
        return ResponseEntity.badRequest().body("Invalid username or password");
    }

    // 회원탈퇴 API
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        userService.deleteUser(username);
        return ResponseEntity.ok("User deleted successfully");
    }

    // 로그아웃은 client 단에서 구현

    // 닉네임 변경 API
    @PatchMapping("/nickname")
    public ResponseEntity<UserResponse> updateNickname(
            @RequestBody NicknameUpdateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {

        // "Bearer " 제거
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        return ResponseEntity.ok(userService.updateNickname(username, request.getNickname()));
    }
}