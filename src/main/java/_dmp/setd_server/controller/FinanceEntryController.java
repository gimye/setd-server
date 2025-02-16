package _dmp.setd_server.controller;

import _dmp.setd_server.dto.request.FinanceEntryRequest;
import _dmp.setd_server.dto.request.FinanceEntryUpdateRequest;
import _dmp.setd_server.dto.response.FinanceEntryResponse;
import _dmp.setd_server.dto.response.MonthlyFinanceResponse;
import _dmp.setd_server.dto.response.TagFinanceStatsResponse;
import _dmp.setd_server.service.FinanceEntryService;
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
@RequiredArgsConstructor
@RequestMapping("/api/finances")
class FinanceEntryController {

    private final FinanceEntryService financeEntryService;
    private final JWTUtil jwtUtil;

    @PostMapping
    public ResponseEntity<List<FinanceEntryResponse>> createFinanceEntry(
            @RequestBody @Valid FinanceEntryRequest request,
            @RequestHeader("Authorization") String authHeader) {

        try{
            String username = jwtUtil.extractUsername(authHeader.substring(7));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(financeEntryService.createFinanceEntry(username, request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FinanceEntryResponse> updateFinanceEntry(
            @PathVariable Long id,
            @RequestBody FinanceEntryUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(financeEntryService.updateFinanceEntry(username, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinanceEntry(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        financeEntryService.deleteFinanceEntry(username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyFinanceResponse> getMonthlyFinances(
            @PathVariable int year,
            @PathVariable int month,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(financeEntryService.getMonthlyFinances(username, year, month));
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<List<FinanceEntryResponse>> getDailyFinances(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return ResponseEntity.ok(financeEntryService.getDailyFinances(username, date));
    }

    @GetMapping("/tag/{year}/{month}/{tagId}")
    public ResponseEntity<TagFinanceStatsResponse> getFinanceStatsByTag(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable Long tagId,
            @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.extractUsername(authHeader.substring(7));

        // 해당 월의 시작일과 종료일 계산
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return ResponseEntity.ok(financeEntryService.getFinanceStatsByTag(username, tagId, start, end));
    }

}
