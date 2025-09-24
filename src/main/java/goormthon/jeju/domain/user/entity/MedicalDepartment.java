package goormthon.jeju.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MedicalDepartment {
    INTERNAL_MEDICINE("내과"),
    OPHTHALMOLOGY("안과"),
    REHABILITATION("재활의학과"),
    ORTHOPEDICS("정형외과"),
    NEUROLOGY("신경과"),
    PSYCHIATRY("정신건강의학과"),
    DERMATOLOGY("피부과"),
    UROLOGY("비뇨기과"),
    ENT("이비인후과"),
    GENERAL_SURGERY("외과");

    private final String displayName;
}