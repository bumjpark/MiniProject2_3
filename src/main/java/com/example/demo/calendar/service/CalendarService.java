package com.example.demo.calendar.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.calendar.entity.Calendar;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.calendar.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    private final CurrentUserResolver currentUserResolver;

    public Calendar create(String name, List<Long> memberIds) {
        List<User> members = resolveMembers(memberIds);

        Long creatorId = currentUserResolver.getCurrentUserId();
        boolean creatorIncluded = members.stream()
                .anyMatch(member -> member.getId().equals(creatorId));
        if (!creatorIncluded) {
            User creator = userRepository.findById(creatorId)
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + creatorId));
            members.add(creator);
        }

        Calendar calendar = Calendar.builder()
                .name(name)
                .members(members)
                .build();
        return calendarRepository.save(calendar);
    }

    // calendarId로 캘린더를 조회하면서, 현재 로그인한 사용자가 그 캘린더의 멤버인지도 함께 검증한다.
    public Calendar getById(Long calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new NoSuchElementException("캘린더를 찾을 수 없습니다. calendarId=" + calendarId));

        Long currentUserId = currentUserResolver.getCurrentUserId();
        boolean isMember = calendar.getMembers().stream()
                .anyMatch(member -> member.getId().equals(currentUserId));
        if (!isMember) {
            throw new AccessDeniedException("이 캘린더에 접근 권한이 없습니다. calendarId=" + calendarId);
        }
        return calendar;
    }

    // 내가 멤버로 속한 캘린더만 조회한다.
    public List<Calendar> getAll() {
        Long currentUserId = currentUserResolver.getCurrentUserId();
        return calendarRepository.findByMembers_Id(currentUserId);
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
        List<User> members = new ArrayList<>();
        for (Long userId : memberIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. id=" + userId));
            members.add(user);
        }
        return members;
    }
}
