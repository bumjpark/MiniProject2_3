package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Calendar;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// MyBatis Mapper로 변경 전
@Repository
public class CalendarRepository {

    private final Map<Long, Calendar> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Calendar save(Calendar calendar) {
        if (calendar.getId() == null) {
            calendar.setId(idGenerator.incrementAndGet());
        }
        store.put(calendar.getId(), calendar);
        return calendar;
    }

    public Optional<Calendar> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Calendar> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
