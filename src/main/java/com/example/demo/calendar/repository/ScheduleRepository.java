package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Schedule;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// MyBatis Mapper로 변경 전
@Repository
public class ScheduleRepository {

    private final Map<Long, Schedule> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Schedule save(Schedule schedule) {
        if (schedule.getId() == null) {
            schedule.setId(idGenerator.incrementAndGet());
        }
        store.put(schedule.getId(), schedule);
        return schedule;
    }

    public Optional<Schedule> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Schedule> findByCalendarId(Long calendarId) {
        return store.values().stream()
                .filter(schedule -> schedule.getCalendarId().equals(calendarId))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
