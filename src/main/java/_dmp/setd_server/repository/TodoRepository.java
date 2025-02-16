package _dmp.setd_server.repository;

import _dmp.setd_server.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT DISTINCT t.date FROM Todo t WHERE t.user.username = :username AND t.date BETWEEN :start AND :end")
    List<LocalDate> findDistinctDatesByUserUsernameAndDateBetween(
            @Param("username") String username,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
    List<Todo> findByUserUsernameAndDate(String username, LocalDate date);
}
