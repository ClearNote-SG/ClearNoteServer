package com.example.clearnote.repository;

import com.example.clearnote.dto.ResponseMeetingDto;
import com.example.clearnote.entity.MeetingMinute;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingMinuteRepository extends JpaRepository<MeetingMinute, Long> {

    @Query("select new com.example.clearnote.dto.ResponseMeetingDto(m.id, m.title, m.createdAt) from MeetingMinute m"
            + " where year(m.createdAt) = :year and month(m.createdAt) = :month and day(m.createdAt) = :day")
    List<ResponseMeetingDto> findMeetingMinutesOnlyId(int year, int month, int day);
}
