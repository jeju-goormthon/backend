package goormthon.jeju.domain.pass.dto;

import goormthon.jeju.domain.pass.entity.Pass;
import goormthon.jeju.domain.pass.entity.PassStatus;
import goormthon.jeju.domain.pass.entity.PassType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class PassResponse {
    private Long id;
    private PassType passType;
    private String startDate;
    private String endDate;
    private Integer price;
    private PassStatus status;
    private boolean valid;

    public static PassResponse from(Pass pass) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new PassResponse(
                pass.getId(),
                pass.getPassType(),
                pass.getStartDate().format(formatter),
                pass.getEndDate().format(formatter),
                pass.getPrice(),
                pass.getStatus(),
                pass.isValid()
        );
    }
}