package _dmp.setd_server.controller;

import _dmp.setd_server.dto.request.TodoRequest;
import _dmp.setd_server.dto.response.TodoResponse;
import _dmp.setd_server.dto.request.TodoUpdateRequest;
import _dmp.setd_server.service.TodoService;
import _dmp.setd_server.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;
    private final JWTUtil jwtUtil;

    // 할 일 생성 API
    @PostMapping
    public ResponseEntity<List<TodoResponse>> createTodo(
            @RequestBody @Valid TodoRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(todoService.createTodo(username, request));
    }

    // 할 일 수정 API
    @PatchMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @RequestBody TodoUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(todoService.updateTodo(username, id, request));
    }

    // 할 일 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        todoService.deleteTodo(username, id);
        return ResponseEntity.ok("delete complete");
    }

//    // 시작일, 종료일에 따른 할 일 받아오기 API
//    @GetMapping
//    public ResponseEntity<List<TodoResponse>> getTodos(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
//            @RequestHeader("Authorization") String authHeader) {
//        String username = jwtUtil.extractUsername(authHeader.substring(7));
//        return ResponseEntity.ok(todoService.getTodos(username, start, end));
//    }

    // 월별 할 일 유무 조회 API
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<LocalDate>> getMonthlyTodoExistence(
            @PathVariable int year,
            @PathVariable int month,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(todoService.getMonthlyTodoExistence(username, year, month));
    }

    // 일별 할 일 상세 조회 API
    @GetMapping("/daily/{year}/{month}/{day}")
    public ResponseEntity<List<TodoResponse>> getDailyTodos(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable int day,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        LocalDate date = LocalDate.of(year, month, day);
        return ResponseEntity.ok(todoService.getDailyTodos(username, date));
    }
}
