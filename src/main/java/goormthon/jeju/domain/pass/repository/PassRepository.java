package goormthon.jeju.domain.pass.repository;

import goormthon.jeju.domain.pass.entity.Pass;
import goormthon.jeju.domain.pass.entity.PassStatus;
import goormthon.jeju.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassRepository extends JpaRepository<Pass, Long> {
    List<Pass> findByUserOrderByCreatedAtDesc(User user);
    Optional<Pass> findByUserAndStatus(User user, PassStatus status);
    boolean existsByUserAndStatus(User user, PassStatus status);
}