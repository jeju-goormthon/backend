package goormthon.jeju.domain.inquiry.dto;

import goormthon.jeju.domain.inquiry.entity.Inquiry;
import goormthon.jeju.domain.inquiry.entity.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class InquiryResponse {
    private Long id;
    private String title;
    private String content;
    private InquiryStatus status;
    private String answer;
    private String createdAt;

    public static InquiryResponse from(Inquiry inquiry) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getStatus(),
                inquiry.getAnswer(),
                inquiry.getCreatedAt().format(formatter)
        );
    }
}