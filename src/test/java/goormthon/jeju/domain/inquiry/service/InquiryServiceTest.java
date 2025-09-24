package goormthon.jeju.domain.inquiry.service;

import goormthon.jeju.domain.inquiry.entity.Inquiry;
import goormthon.jeju.domain.inquiry.entity.InquiryStatus;
import goormthon.jeju.domain.inquiry.repository.InquiryRepository;
import goormthon.jeju.domain.user.entity.LoginType;
import goormthon.jeju.domain.user.entity.MedicalDepartment;
import goormthon.jeju.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @Mock
    private InquiryRepository inquiryRepository;

    @InjectMocks
    private InquiryService inquiryService;

    @Test
    @DisplayName("문의 생성 성공")
    void createInquiry_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .title("예약 관련 문의")
                .content("예약을 취소하고 싶습니다.")
                .build();

        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(inquiry);

        Inquiry createdInquiry = inquiryService.createInquiry(user, "예약 관련 문의", "예약을 취소하고 싶습니다.");

        assertThat(createdInquiry).isNotNull();
        assertThat(createdInquiry.getTitle()).isEqualTo("예약 관련 문의");
        assertThat(createdInquiry.getContent()).isEqualTo("예약을 취소하고 싶습니다.");
        assertThat(createdInquiry.getStatus()).isEqualTo(InquiryStatus.PENDING);
        verify(inquiryRepository, times(1)).save(any(Inquiry.class));
    }

    @Test
    @DisplayName("사용자별 문의 목록 조회 성공")
    void getInquiriesByUser_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Inquiry inquiry1 = Inquiry.builder()
                .user(user)
                .title("문의1")
                .content("내용1")
                .build();

        Inquiry inquiry2 = Inquiry.builder()
                .user(user)
                .title("문의2")
                .content("내용2")
                .build();

        when(inquiryRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(inquiry1, inquiry2));

        List<Inquiry> inquiries = inquiryService.getInquiriesByUser(user);

        assertThat(inquiries).hasSize(2);
        assertThat(inquiries.get(0).getTitle()).isEqualTo("문의1");
        assertThat(inquiries.get(1).getTitle()).isEqualTo("문의2");
        verify(inquiryRepository, times(1)).findByUserOrderByCreatedAtDesc(user);
    }

    @Test
    @DisplayName("문의 답변 성공")
    void answerInquiry_Success() {
        User user = User.builder()
                .phoneNumber("01012345678")
                .medicalDepartment(MedicalDepartment.INTERNAL_MEDICINE)
                .loginType(LoginType.NORMAL)
                .build();

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .title("예약 관련 문의")
                .content("예약을 취소하고 싶습니다.")
                .build();

        when(inquiryRepository.findById(1L)).thenReturn(java.util.Optional.of(inquiry));

        inquiryService.answerInquiry(1L, "예약 취소는 마이페이지에서 가능합니다.");

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.ANSWERED);
        assertThat(inquiry.getAnswer()).isEqualTo("예약 취소는 마이페이지에서 가능합니다.");
    }
}