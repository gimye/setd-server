package _dmp.setd_server.controller;

import _dmp.setd_server.dto.request.TagRequest;
import _dmp.setd_server.dto.response.TagResponse;
import _dmp.setd_server.service.TagService;
import _dmp.setd_server.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final JWTUtil jwtUtil;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
            @RequestBody @Valid TagRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.createTag(username, request));
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags(
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(tagService.getAllTags(username));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Long id,
            @RequestBody @Valid TagRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(tagService.updateTag(username, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        tagService.deleteTag(username, id);
        return ResponseEntity.noContent().build();
    }
}
