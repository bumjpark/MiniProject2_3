package com.example.demo.calendar.service;

import com.example.demo.calendar.dto.ScheduleRequest;
import com.example.demo.calendar.entity.Schedule;
import com.example.demo.calendar.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// 일정 비즈니스 로직
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CalendarService calendarService;

    public Schedule create(Long calendarId, ScheduleRequest request) {
        calendarService.getById(calendarId);
        requireTitle(request);

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
                .participants(request.getParticipants() != null ? request.getParticipants() : new ArrayList<>())
                .label(request.getLabel())
                .build();

        return scheduleRepository.save(schedule);
    }

    public Schedule getById(Long calendarId, Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("일정을 찾을 수 없습니다. id=" + id));
        validateBelongsToCalendar(schedule, calendarId);
        return schedule;
    }

    public List<Schedule> getAllByCalendar(Long calendarId) {
        calendarService.getById(calendarId);
        return scheduleRepository.findByCalendarId(calendarId);
    }

    public Schedule update(Long calendarId, Long id, ScheduleRequest request) {
        Schedule schedule = getById(calendarId, id);
        requireTitle(request);

        schedule.setTitle(request.getTitle());
        if (request.getStartDateTime() != null) {
            schedule.setStartDateTime(request.getStartDateTime());
        }
        if (request.getEndDateTime() != null) {
            schedule.setEndDateTime(request.getEndDateTime());
        }
        if (request.getParticipants() != null) {
            schedule.setParticipants(request.getParticipants());
        }
        if (request.getLabel() != null) {
            schedule.setLabel(request.getLabel());
        }

        return scheduleRepository.save(schedule);
    }

    public void delete(Long calendarId, Long id) {
        Schedule schedule = getById(calendarId, id);
        scheduleRepository.deleteById(schedule.getId());
    }

    private void requireTitle(ScheduleRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
    }

    private void validateBelongsToCalendar(Schedule schedule, Long calendarId) {
        if (!schedule.getCalendarId().equals(calendarId)) {
            throw new NoSuchElementException("해당 캘린더에서 일정을 찾을 수 없습니다. id=" + schedule.getId());
        }
    }
}
