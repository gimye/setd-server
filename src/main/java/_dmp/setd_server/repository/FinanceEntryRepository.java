package _dmp.setd_server.repository;

import _dmp.setd_server.dto.response.TagFinanceStatsResponse;
import _dmp.setd_server.entity.FinanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinanceEntryRepository extends JpaRepository<FinanceEntry, Long> {

    List<FinanceEntry> findByUserUsernameAndDateBetween(String username, LocalDate start, LocalDate end);

    @Query("SELECT DISTINCT f.date FROM FinanceEntry f WHERE f.user.username = :username AND f.date BETWEEN :start AND :end")
    List<LocalDate> findDistinctDatesByUserUsernameAndDateBetween(
            @Param("username") String username,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT new _dmp.setd_server.dto.response.TagFinanceStatsResponse(t.id, t.name, " +
            "COALESCE(SUM(CASE WHEN f.type = 'INCOME' THEN f.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN f.type = 'EXPENSE' THEN f.amount ELSE 0 END), 0)) " +
            "FROM FinanceEntry f JOIN f.tag t " +
            "WHERE f.user.username = :username AND t.id = :tagId AND f.date BETWEEN :start AND :end " +
            "GROUP BY t.id, t.name")
    List<TagFinanceStatsResponse> findFinanceStatsByTag(
            @Param("username") String username,
            @Param("tagId") Long tagId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);


    List<FinanceEntry> findByUserUsernameAndDate(String username, LocalDate date);

    @Query("SELECT SUM(CASE WHEN f.type = 'EXPENSE' THEN f.amount ELSE 0 END) FROM FinanceEntry f WHERE f.user.username = :username")
    BigDecimal getTotalExpenseByUsername(@Param("username") String username);

    @Query("SELECT SUM(CASE WHEN f.type = 'INCOME' THEN f.amount ELSE 0 END) FROM FinanceEntry f WHERE f.user.username = :username")
    BigDecimal getTotalIncomeByUsername(@Param("username") String username);

    @Query("SELECT SUM(CASE WHEN f.type = 'EXPENSE' THEN f.amount ELSE 0 END) FROM FinanceEntry f WHERE f.user.username = :username AND f.date = :date")
    BigDecimal getDailyExpenseByUsername(@Param("username") String username, @Param("date") LocalDate date);

    @Query("SELECT SUM(CASE WHEN f.type = 'INCOME' THEN f.amount ELSE 0 END) FROM FinanceEntry f WHERE f.user.username = :username AND f.date = :date")
    BigDecimal getDailyIncomeByUsername(@Param("username") String username, @Param("date") LocalDate date);

}
