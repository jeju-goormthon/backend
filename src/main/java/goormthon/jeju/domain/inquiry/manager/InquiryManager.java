package goormthon.jeju.domain.inquiry.manager;

import goormthon.jeju.domain.inquiry.dto.InquiryResponse;
import goormthon.jeju.domain.inquiry.entity.Inquiry;
import goormthon.jeju.domain.inquiry.service.InquiryService;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryManager {

    private final InquiryService inquiryService;
    private final UserService userService;

    @Transactional
    public InquiryResponse createInquiry(Long userId, String title, String content) {
        User user = userService.findById(userId);
        Inquiry inquiry = inquiryService.createInquiry(user, title, content);
        return InquiryResponse.from(inquiry);
    }

    public List<InquiryResponse> getMyInquiries(Long userId) {
        User user = userService.findById(userId);
        List<Inquiry> inquiries = inquiryService.getInquiriesByUser(user);
        return inquiries.stream()
                .map(InquiryResponse::from)
                .toList();
    }

    public InquiryResponse getInquiryDetail(Long inquiryId) {
        Inquiry inquiry = inquiryService.findById(inquiryId);
        return InquiryResponse.from(inquiry);
    }

    @Transactional
    public void answerInquiry(Long adminId, Long inquiryId, String answer) {
        userService.checkAdmin(adminId);
        inquiryService.answerInquiry(inquiryId, answer);
    }

    public List<InquiryResponse> getAllInquiries(Long adminId) {
        userService.checkAdmin(adminId);
        return inquiryService.getAllInquiries().stream()
                .map(InquiryResponse::from)
                .toList();
    }
}