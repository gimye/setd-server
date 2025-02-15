package _dmp.setd_server.repository;

import _dmp.setd_server.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserUsernameAndStartDateBetween(String username, LocalDate start, LocalDate end);
}
