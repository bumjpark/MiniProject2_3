package com.example.demo.calendar.repository;

import com.example.demo.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByMembers_Id(Long userId);
}
