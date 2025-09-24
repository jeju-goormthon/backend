package goormthon.jeju.domain.pass.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PassType {
    ONE_MONTH(1, 15000),
    THREE_MONTHS(3, 40000),
    SIX_MONTHS(6, 75000);

    private final int months;
    private final int price;
}