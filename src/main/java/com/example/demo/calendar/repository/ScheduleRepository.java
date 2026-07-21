package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByCalendarId(Long calendarId);

    void deleteByCalendarId(Long calendarId);
}
