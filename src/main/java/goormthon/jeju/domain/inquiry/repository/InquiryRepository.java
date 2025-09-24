package goormthon.jeju.domain.inquiry.repository;

import goormthon.jeju.domain.inquiry.entity.Inquiry;
import goormthon.jeju.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByUserOrderByCreatedAtDesc(User user);
}