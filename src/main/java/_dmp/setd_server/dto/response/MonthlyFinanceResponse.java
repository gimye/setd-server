package _dmp.setd_server.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MonthlyFinanceResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    public MonthlyFinanceResponse(BigDecimal totalIncome, BigDecimal totalExpense) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }
}
