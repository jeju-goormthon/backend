package goormthon.jeju.domain.pass.dto;

import goormthon.jeju.domain.pass.entity.PassType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePassRequest {

    @NotNull(message = "정기권 타입은 필수입니다.")
    private PassType passType;
}