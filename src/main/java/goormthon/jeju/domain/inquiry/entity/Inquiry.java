package goormthon.jeju.domain.inquiry.entity;

import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InquiryStatus status;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Builder
    public Inquiry(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.status = InquiryStatus.PENDING;
    }

    public void answer(String answer) {
        this.answer = answer;
        this.status = InquiryStatus.ANSWERED;
    }
}