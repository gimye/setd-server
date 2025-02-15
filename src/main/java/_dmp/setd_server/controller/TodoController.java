package _dmp.setd_server.controller;

import _dmp.setd_server.dto.TodoRequest;
import _dmp.setd_server.dto.TodoResponse;
import _dmp.setd_server.dto.TodoUpdateRequest;
import _dmp.setd_server.service.TodoService;
import _dmp.setd_server.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @RequestBody @Valid TodoRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(todoService.createTodo(username, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @RequestBody TodoUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(todoService.updateTodo(username, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        todoService.deleteTodo(username, id);
        return ResponseEntity.ok("delete complete");
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(todoService.getTodos(username, start, end));
    }
}
