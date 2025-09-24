package goormthon.jeju.domain.inquiry.service;

import goormthon.jeju.domain.inquiry.entity.Inquiry;
import goormthon.jeju.domain.inquiry.repository.InquiryRepository;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public Inquiry createInquiry(User user, String title, String content) {
        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .title(title)
                .content(content)
                .build();

        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getInquiriesByUser(User user) {
        return inquiryRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Inquiry findById(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new GlobalException(ErrorCode.INQUIRY_NOT_FOUND));
    }

    @Transactional
    public void answerInquiry(Long inquiryId, String answer) {
        Inquiry inquiry = findById(inquiryId);
        inquiry.answer(answer);
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }
}