package _dmp.setd_server.dto;

import _dmp.setd_server.entity.FinanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FinanceEntryRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private FinanceType type;

    private Long tagId;

    @NotNull
    private String title;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private String repeatDays;
}
