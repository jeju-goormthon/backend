package goormthon.jeju.domain.pass.entity;

import goormthon.jeju.domain.payment.entity.Payment;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "passes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pass extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PassType passType;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PassStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Builder
    public Pass(User user, PassType passType, LocalDateTime startDate, Integer price, Payment payment) {
        this.user = user;
        this.passType = passType;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(passType.getMonths());
        this.price = price;
        this.status = PassStatus.ACTIVE;
        this.payment = payment;
    }

    public boolean isValid() {
        return this.status == PassStatus.ACTIVE && LocalDateTime.now().isBefore(this.endDate);
    }

    public void expire() {
        this.status = PassStatus.EXPIRED;
    }

    public void cancel() {
        this.status = PassStatus.CANCELLED;
    }
}