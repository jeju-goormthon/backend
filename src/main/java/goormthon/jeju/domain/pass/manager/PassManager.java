package goormthon.jeju.domain.pass.manager;

import goormthon.jeju.domain.pass.dto.PassResponse;
import goormthon.jeju.domain.pass.entity.Pass;
import goormthon.jeju.domain.pass.entity.PassType;
import goormthon.jeju.domain.pass.service.PassService;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.domain.user.service.UserService;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassManager {

    private final PassService passService;
    private final UserService userService;

    /**
     * 정기권 생성 (단순 CRUD - 결제와 분리)
     */
    @Transactional
    public PassResponse createPass(Long userId, PassType passType) {
        User user = userService.findById(userId);

        // 활성 정기권 중복 체크
        if (passService.hasActivePass(user)) {
            throw new GlobalException(ErrorCode.PASS_ALREADY_EXISTS);
        }

        Pass pass = passService.createPass(user, passType);
        return PassResponse.from(pass);
    }

    public List<PassResponse> getMyPasses(Long userId) {
        User user = userService.findById(userId);
        List<Pass> passes = passService.getPassesByUser(user);
        return passes.stream()
                .map(PassResponse::from)
                .toList();
    }

    public PassResponse getActivePass(Long userId) {
        User user = userService.findById(userId);
        Pass pass = passService.getActivePass(user);
        return PassResponse.from(pass);
    }

    public boolean hasActivePass(Long userId) {
        User user = userService.findById(userId);
        return passService.hasActivePass(user);
    }

    @Transactional
    public void cancelPass(Long userId, Long passId) {
        User user = userService.findById(userId);
        Pass pass = passService.findById(passId);

        if (!pass.getUser().getId().equals(user.getId())) {
            throw new GlobalException(ErrorCode.PASS_NOT_FOUND);
        }

        passService.cancelPass(passId);
    }
}