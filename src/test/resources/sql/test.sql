-- todo_db 테스트 픽스처. TodoIntegrationTest가 @Sql로 각 테스트 실행 전에 이 파일을 실행함

DELETE FROM todo;
DELETE FROM todo_list;
DELETE FROM category;

-- user/calendar/schedule은 다른 기능(캘린더)도 같이 쓰는 테이블이라, 이 픽스처가 만든 것만 정리
DELETE FROM calendar_members WHERE user_id IN (1001, 1002);
DELETE FROM schedule WHERE calendar_id IN (SELECT calendar_id FROM calendar WHERE name = '미프2 3조 캘린더');
DELETE FROM calendar WHERE name = '미프2 3조 캘린더';
DELETE FROM user WHERE id IN (1001, 1002);

-- user
INSERT INTO user (id, email, password, name, refresh_token) VALUES
    (1001, 'aa@test.com', '1234', 'ABC', NULL),
    (1002, 'bb@test.com', '1234', 'QWE', NULL);

-- calendar
INSERT INTO calendar (name) VALUES ('미프2 3조 캘린더');

-- calendar_members
INSERT INTO calendar_members (calendar_id, user_id)
SELECT c.calendar_id, u.id
FROM calendar c, user u
WHERE c.name = '미프2 3조 캘린더' AND u.id IN (1001, 1002);

-- schedule
INSERT INTO schedule (calendar_id, title, start_date_time, end_date_time, label)
SELECT c.calendar_id, '주간 회의', '2026-07-23 10:00:00', '2026-07-23 11:00:00', 'BLUE'
FROM calendar c WHERE c.name = '미프2 3조 캘린더';

-- category
INSERT INTO category (category_id, category_name) VALUES
    (2001, '중요'),
    (2002, '업무'),
    (2003, '개인');

-- todo_list
INSERT INTO todo_list (list_id, user_id, name) VALUES
    (3001, 1001, '사용자 1 업무'),
    (3002, 1001, '사용자 1 개인'),
    (3003, 1002, '사용자 2 업무');

-- todo
INSERT INTO todo (todo_id, user_id, list_id, category_id, deadline, content, todo_completed) VALUES
    (4001, 1001, 3001, 2001, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '중요 업무 마무리', FALSE),
    (4002, 1001, 3001, 2002, DATE_ADD(CURDATE(), INTERVAL 7 DAY), '완료된 업무 확인', TRUE),
    (4003, 1001, NULL, 2003, NULL, '목록 없는 개인 Todo', FALSE),
    (4004, 1001, 3002, NULL, CURDATE(), '카테고리 없는 오늘 Todo', FALSE),
    (4005, 1002, 3003, 2001, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '다른 사용자의 Todo', FALSE),
    (4006, 1002, NULL, NULL, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '기한이 지난 Todo', FALSE);
