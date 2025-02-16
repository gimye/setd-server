package _dmp.setd_server.dto;

import _dmp.setd_server.entity.FinanceEntry;
import _dmp.setd_server.entity.FinanceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FinanceEntryResponse {
    private Long id;
    private String title;
    private BigDecimal amount;
    private FinanceType type;
    private Long tagId;
    private String tagName;
    private LocalDate date;

    public static FinanceEntryResponse from(FinanceEntry entry) {
        FinanceEntryResponse response = new FinanceEntryResponse();
        response.setId(entry.getId());
        response.setTitle(entry.getTitle());
        response.setAmount(entry.getAmount());
        response.setType(entry.getType());
        response.setDate(entry.getDate());
        if (entry.getTag() != null) {
            response.setTagId(entry.getTag().getId());
            response.setTagName(entry.getTag().getName());
        }
        return response;
    }
}
