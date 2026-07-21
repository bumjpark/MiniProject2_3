package com.example.demo.calendar.service;

import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// 캘린더 비즈니스 로직 
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public Calendar create(String name, List<String> members) {
        Calendar calendar = Calendar.builder()
                .name(name)
                .members(members != null ? members : new ArrayList<>())
                .build();
        return calendarRepository.save(calendar);
    }

    public Calendar getById(Long id) {
        return calendarRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("캘린더를 찾을 수 없습니다. id=" + id));
    }

    public List<Calendar> getAll() {
        return calendarRepository.findAll();
    }

    public Calendar update(Long id, String name, List<String> members) {
        Calendar calendar = getById(id);
        calendar.setName(name);
        if (members != null) {
            calendar.setMembers(members);
        }
        return calendarRepository.save(calendar);
    }

    public void delete(Long id) {
        getById(id);
        calendarRepository.deleteById(id);
    }
}
