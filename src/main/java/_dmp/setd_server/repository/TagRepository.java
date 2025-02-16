package _dmp.setd_server.repository;

import _dmp.setd_server.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUserUsername(String username);
    Optional<Tag> findByIdAndUserUsername(Long id, String username);
    boolean existsByNameAndUserUsername(String name, String username);
}
