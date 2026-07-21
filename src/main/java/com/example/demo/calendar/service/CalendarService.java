package com.example.demo.calendar.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.calendar.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// 캘린더 비즈니스 로직
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    public Calendar create(String name, List<Long> memberIds) {
        Calendar calendar = Calendar.builder()
                .name(name)
                .members(resolveMembers(memberIds))
                .build();
        return calendarRepository.save(calendar);
    }

    public Calendar getById(Long calendarId) {
        return calendarRepository.findById(calendarId)
                .orElseThrow(() -> new NoSuchElementException("캘린더를 찾을 수 없습니다. calendarId=" + calendarId));
    }

    public List<Calendar> getAll() {
        return calendarRepository.findAll();
    }

    public Calendar update(Long calendarId, String name, List<Long> memberIds) {
        Calendar calendar = getById(calendarId);
        if (name != null) {
            calendar.setName(name);
        }
        if (memberIds != null) {
            calendar.setMembers(resolveMembers(memberIds));
        }
        return calendarRepository.save(calendar);
    }

    @Transactional
    public void delete(Long calendarId) {
        getById(calendarId);
        scheduleRepository.deleteByCalendarId(calendarId);
        calendarRepository.deleteById(calendarId);
    }

    public Calendar addMember(Long calendarId, Long userId) {
        Calendar calendar = getById(calendarId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + userId));

        boolean alreadyMember = calendar.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
        if (!alreadyMember) {
            calendar.getMembers().add(user);
        }
        return calendarRepository.save(calendar);
    }

    public Calendar removeMember(Long calendarId, Long userId) {
        Calendar calendar = getById(calendarId);
        calendar.getMembers().removeIf(member -> member.getId().equals(userId));
        return calendarRepository.save(calendar);
    }

    private List<User> resolveMembers(List<Long> memberIds) {
        if (memberIds == null) {
            return new ArrayList<>();
        }
        return memberIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + userId)))
                .toList();
    }
}
