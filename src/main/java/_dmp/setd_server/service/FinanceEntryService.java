package _dmp.setd_server.service;

import _dmp.setd_server.dto.request.FinanceEntryRequest;
import _dmp.setd_server.dto.request.FinanceEntryUpdateRequest;
import _dmp.setd_server.dto.response.FinanceEntryResponse;
import _dmp.setd_server.dto.response.MonthlyFinanceResponse;
import _dmp.setd_server.dto.response.TagFinanceStatsResponse;
import _dmp.setd_server.entity.FinanceEntry;
import _dmp.setd_server.entity.FinanceType;
import _dmp.setd_server.entity.Tag;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.repository.FinanceEntryRepository;
import _dmp.setd_server.repository.TagRepository;
import _dmp.setd_server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceEntryService {
    private final FinanceEntryRepository financeEntryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Transactional
    public List<FinanceEntryResponse> createFinanceEntry(String username, FinanceEntryRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Tag tag = tagRepository.findByIdAndUserUsername(request.getTagId(), user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid tag ID for this user"));

        final User finalUser = user;
        final Tag finalTag = tag;

        List<LocalDate> dates = calculateRecurringDates(
                request.getStartDate(),
                request.getEndDate(),
                request.getRepeatDays()
        );

        List<FinanceEntry> entries = dates.stream()
                .map(date -> {
                    FinanceEntry entry = new FinanceEntry();
                    entry.setUser(finalUser);
                    entry.setTitle(request.getTitle());
                    entry.setAmount(request.getAmount());
                    entry.setType(request.getType());
                    entry.setTag(finalTag);
                    entry.setDate(date);
                    entry.setOriginalStartDate(request.getStartDate());
                    entry.setOriginalEndDate(request.getEndDate());
                    entry.setRepeatDays(request.getRepeatDays());
                    return entry;
                })
                .toList();

        List<FinanceEntry> savedEntries = financeEntryRepository.saveAll(entries);
        return savedEntries.stream()
                .map(FinanceEntryResponse::from)
                .toList();
    }

    @Transactional
    public FinanceEntryResponse updateFinanceEntry(String username, Long entryId, FinanceEntryUpdateRequest request) {
        FinanceEntry entry = financeEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Finance entry not found"));

        if (!entry.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You don't have permission to update this entry");
        }

        if (request.getTitle() != null) {
            entry.setTitle(request.getTitle());
        }
        if (request.getAmount() != null) {
            entry.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            entry.setType(request.getType());
        }
        if (request.getTagId() != null) {
            Tag tag = tagRepository.findById(request.getTagId())
                    .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
            entry.setTag(tag);
        }
        if (request.getDate() != null) {
            entry.setDate(request.getDate());
        }

        FinanceEntry updatedEntry = financeEntryRepository.save(entry);
        return FinanceEntryResponse.from(updatedEntry);
    }

    @Transactional
    public void deleteFinanceEntry(String username, Long entryId) {
        FinanceEntry entry = financeEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Finance entry not found"));

        if (!entry.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You don't have permission to delete this entry");
        }

        financeEntryRepository.delete(entry);
    }

    public MonthlyFinanceResponse getMonthlyFinances(String username, int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<FinanceEntry> entries = financeEntryRepository.findByUserUsernameAndDateBetween(username, startOfMonth, endOfMonth);

        BigDecimal totalIncome = entries.stream()
                .filter(entry -> entry.getType() == FinanceType.INCOME)
                .map(FinanceEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = entries.stream()
                .filter(entry -> entry.getType() == FinanceType.EXPENSE)
                .map(FinanceEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlyFinanceResponse(totalIncome, totalExpense);
    }

    public List<FinanceEntryResponse> getDailyFinances(String username, LocalDate date) {
        List<FinanceEntry> entries = financeEntryRepository.findByUserUsernameAndDate(username, date);
        return entries.stream()
                .map(FinanceEntryResponse::from)
                .toList();
    }

    // 날짜 계산 함수
    private List<LocalDate> calculateRecurringDates(LocalDate start, LocalDate end, String repeatDays) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start;

        while (!current.isAfter(end)) {
            if (shouldIncludeDate(current, repeatDays, start)) {
                dates.add(current);
            }
            current = current.plusDays(1);
        }

        return dates;
    }

    private boolean shouldIncludeDate(LocalDate date, String repeatDays, LocalDate start) {
        if (repeatDays == null || repeatDays.isEmpty()) {
            return date.equals(start);
        }

        int dayOfWeek = date.getDayOfWeek().getValue() % 7;
        return repeatDays.contains(String.valueOf(dayOfWeek));
    }

    public TagFinanceStatsResponse getFinanceStatsByTag(String username, Long tagId, LocalDate start, LocalDate end) {
        // 먼저 태그의 존재 여부만 확인
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + tagId));

        // 그 다음 해당 태그가 사용자에게 속하는지 확인
        if (!tag.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("Tag with id: " + tagId + " does not belong to user: " + username);
        }

        List<TagFinanceStatsResponse> stats = financeEntryRepository.findFinanceStatsByTag(username, tagId, start, end);

        if (stats.isEmpty()) {
            return new TagFinanceStatsResponse(tagId, tag.getName(), BigDecimal.ZERO, BigDecimal.ZERO);
        } else {
            return stats.get(0);
        }
    }




}
