-- 노선 초기 데이터 삽입 (약 30개)

-- 내과 노선들 (10개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'INTERNAL_MEDICINE', '08:00:00', '08:30:00', 30, 20, 3, '제주시청 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'INTERNAL_MEDICINE', '09:00:00', '09:30:00', 30, 25, 8, '서귀포시청 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'INTERNAL_MEDICINE', '10:30:00', '11:00:00', 30, 22, 5, '애월읍사무소 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'INTERNAL_MEDICINE', '13:30:00', '14:00:00', 30, 18, 12, '한림읍사무소 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'INTERNAL_MEDICINE', '15:00:00', '15:30:00', 30, 24, 7, '중문관광단지 정류장', NOW(), NOW());

-- 피부과 노선들 (5개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'DERMATOLOGY', '09:30:00', '10:00:00', 30, 16, 4, '성산포항 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'DERMATOLOGY', '11:00:00', '11:30:00', 30, 20, 9, '표선해수욕장 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'DERMATOLOGY', '14:30:00', '15:00:00', 30, 18, 6, '남원읍사무소 정류장', NOW(), NOW());

-- 정형외과 노선들 (4개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'ORTHOPEDICS', '08:30:00', '09:00:00', 30, 15, 2, '화북항 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'ORTHOPEDICS', '13:00:00', '13:30:00', 30, 22, 8, '조천읍사무소 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'ORTHOPEDICS', '16:00:00', '16:30:00', 30, 20, 11, '구좌읍사무소 정류장', NOW(), NOW());

-- 신경과 노선들 (3개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'NEUROLOGY', '10:30:00', '11:00:00', 30, 12, 3, '우도면사무소 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'NEUROLOGY', '14:30:00', '15:00:00', 30, 16, 7, '마라도항 정류장', NOW(), NOW());

-- 안과 노선들 (2개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'OPHTHALMOLOGY', '09:00:00', '09:30:00', 30, 14, 4, '대정읍사무소 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'OPHTHALMOLOGY', '11:30:00', '12:00:00', 30, 18, 8, '안덕면사무소 정류장', NOW(), NOW());

-- 이비인후과 노선들 (2개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'ENT', '08:00:00', '08:30:00', 30, 12, 2, '한경면사무소 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'ENT', '15:30:00', '16:00:00', 30, 16, 5, '추자면사무소 정류장', NOW(), NOW());

-- 외과 노선들 (2개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'GENERAL_SURGERY', '07:30:00', '08:00:00', 30, 10, 1, '노형동 주민센터 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'GENERAL_SURGERY', '12:30:00', '13:00:00', 30, 14, 4, '이도2동 주민센터 정류장', NOW(), NOW());

-- 비뇨의학과 노선들 (2개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'UROLOGY', '10:00:00', '10:30:00', 30, 15, 3, '삼양동 주민센터 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'UROLOGY', '16:30:00', '17:00:00', 30, 18, 6, '도두동 주민센터 정류장', NOW(), NOW());

-- 정신건강의학과 노선들 (2개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'PSYCHIATRY', '13:30:00', '14:00:00', 30, 16, 5, '용담2동 주민센터 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'PSYCHIATRY', '17:00:00', '17:30:00', 30, 20, 9, '건입동 주민센터 정류장', NOW(), NOW());

-- 재활의학과 노선들 (2개)
INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'REHABILITATION', '09:30:00', '10:00:00', 30, 12, 2, '아라동 주민센터 정류장', NOW(), NOW());

INSERT INTO routes (hospital_name, medical_department, start_time, end_time, expected_minutes, total_seats, booked_seats, pickup_location, created_at, updated_at)
VALUES ('제주대학교병원', 'REHABILITATION', '14:00:00', '14:30:00', 30, 14, 4, '오라2동 주민센터 정류장', NOW(), NOW());