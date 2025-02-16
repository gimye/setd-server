package _dmp.setd_server.dto.request;

import _dmp.setd_server.entity.FinanceType;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FinanceEntryUpdateRequest {
    @Positive
    private BigDecimal amount;

    private String title;

    private FinanceType type;

    private Long tagId;

    private LocalDate date;
}
