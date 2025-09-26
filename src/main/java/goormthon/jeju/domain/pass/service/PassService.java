package goormthon.jeju.domain.pass.service;

import goormthon.jeju.domain.pass.entity.Pass;
import goormthon.jeju.domain.pass.entity.PassStatus;
import goormthon.jeju.domain.pass.entity.PassType;
import goormthon.jeju.domain.pass.repository.PassRepository;
import goormthon.jeju.domain.user.entity.User;
import goormthon.jeju.global.exception.ErrorCode;
import goormthon.jeju.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassService {

    private final PassRepository passRepository;

    @Transactional
    public Pass createPass(User user, PassType passType) {
        if (passRepository.existsByUserAndStatus(user, PassStatus.ACTIVE)) {
            throw new GlobalException(ErrorCode.PASS_ALREADY_EXISTS);
        }

        Pass pass = Pass.builder()
                .user(user)
                .passType(passType)
                .startDate(LocalDateTime.now())
                .price(passType.getPrice())
                .build();

        return passRepository.save(pass);
    }

    public List<Pass> getPassesByUser(User user) {
        return passRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Pass findById(Long passId) {
        return passRepository.findById(passId)
                .orElseThrow(() -> new GlobalException(ErrorCode.PASS_NOT_FOUND));
    }

    public Pass getActivePass(User user) {
        return passRepository.findByUserAndStatus(user, PassStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException(ErrorCode.PASS_NOT_FOUND));
    }

    public boolean hasActivePass(User user) {
        return passRepository.existsByUserAndStatus(user, PassStatus.ACTIVE);
    }

    @Transactional
    public void expirePass(Long passId) {
        Pass pass = findById(passId);
        pass.expire();
    }

    @Transactional
    public void cancelPass(Long passId) {
        Pass pass = findById(passId);
        pass.cancel();
    }

}