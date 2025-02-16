package _dmp.setd_server.dto.response;

import lombok.Value;
import java.math.BigDecimal;

@Value
public class TagFinanceStatsResponse {
    Long tagId;
    String tagName;
    BigDecimal totalIncome;
    BigDecimal totalExpense;
}
