package com.example.demo.calendar.service;

import com.example.demo.calendar.dto.ScheduleRequest;
import com.example.demo.calendar.entity.Schedule;
import com.example.demo.calendar.repository.ScheduleRepository;
import com.example.demo.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

// 일정 비즈니스 로직
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CalendarService calendarService;
    private final TodoRepository todoRepository;

    public Schedule create(Long calendarId, ScheduleRequest request) {
        calendarService.getById(calendarId);

        LocalDateTime start = request.getStartDateTime() != null
                ? request.getStartDateTime()
                : LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime end = request.getEndDateTime() != null
                ? request.getEndDateTime()
                : start.plusHours(1);

        Schedule schedule = Schedule.builder()
                .calendarId(calendarId)
                .title(request.getTitle())
                .startDateTime(start)
                .endDateTime(end)
                .label(request.getLabel())
                .build();

        return scheduleRepository.save(schedule);
    }

    public Schedule getById(Long calendarId, Long scheduleId) {
        calendarService.getById(calendarId);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("일정을 찾을 수 없습니다. scheduleId=" + scheduleId));
        validateBelongsToCalendar(schedule, calendarId);
        return schedule;
    }

    public List<Schedule> getAllByCalendar(Long calendarId) {
        calendarService.getById(calendarId);
        return scheduleRepository.findByCalendarId(calendarId);
    }

    public Schedule update(Long calendarId, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = getById(calendarId, scheduleId);

        schedule.setTitle(request.getTitle());
        if (request.getStartDateTime() != null) {
            schedule.setStartDateTime(request.getStartDateTime());
        }
        if (request.getEndDateTime() != null) {
            schedule.setEndDateTime(request.getEndDateTime());
        }
        if (request.getLabel() != null) {
            schedule.setLabel(request.getLabel());
        }

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void delete(Long calendarId, Long scheduleId) {
        Schedule schedule = getById(calendarId, scheduleId);

        // 일정을 지운다고 남의 Todo까지 사라지면 안 되니, Todo는 남기고 연결만 끊는다.
        todoRepository.findByScheduleId(scheduleId)
                .ifPresent(todo -> {
                    todo.linkSchedule(null);
                    todoRepository.save(todo);
                });

        scheduleRepository.deleteById(schedule.getScheduleId());
    }

    private void validateBelongsToCalendar(Schedule schedule, Long calendarId) {
        if (!schedule.getCalendarId().equals(calendarId)) {
            throw new NoSuchElementException("해당 캘린더에서 일정을 찾을 수 없습니다. scheduleId=" + schedule.getScheduleId());
        }
    }
}
