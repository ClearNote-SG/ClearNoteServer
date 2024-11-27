package com.example.clearnote.application;

import com.example.clearnote.dto.ResponseMeetingDto;
import com.example.clearnote.entity.MeetingMinute;
import com.example.clearnote.repository.MeetingMinuteRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingMinuteService {
    private final MeetingMinuteRepository meetingMinuteRepository;

    @Transactional
    public Long createMeetingMinute(){

        String content = null;
        String title = null;
        String summary = null;

        MeetingMinute savedMeetingMinute = meetingMinuteRepository.save(MeetingMinute.builder()
                .title(title)
                .content(content)
                .summary(summary)
                .audioFile(null)
                .build());
        return savedMeetingMinute.getId(); // 생성된 ID 반환
    }

    @Transactional
    @Async("asyncExecutor")
    public void uploadVoice(Long id, byte[] voiceFile){

        MeetingMinute meetingMinute = meetingMinuteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MeetingMinute not found with id: " + id));

        // 새로운 값으로 업데이트

        meetingMinute = MeetingMinute.builder()
                .id(meetingMinute.getId()) // 기존 ID 유지
                .title(meetingMinute.getTitle()) // null 체크 후 업데이트
                .content(meetingMinute.getContent())
                .summary(meetingMinute.getSummary())
                .audioFile(voiceFile != null ? voiceFile : meetingMinute.getAudioFile()) // 오디오 파일도 그대로 유지
                .build();

        meetingMinuteRepository.save(meetingMinute);
    }

    @Transactional
    @Async("asyncExecutor")
    public void uploadContent(Long id, String content) {
        // ID로 기존 엔티티 조회
        MeetingMinute meetingMinute = meetingMinuteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MeetingMinute not found with id: " + id));

        // 새로운 값으로 업데이트
        meetingMinute = MeetingMinute.builder()
                .id(meetingMinute.getId()) // 기존 ID 유지
                .title(meetingMinute.getTitle()) // null 체크 후 업데이트
                .content(content != null ? content : meetingMinute.getContent())
                .summary(meetingMinute.getSummary())
                .audioFile(meetingMinute.getAudioFile()) // 오디오 파일도 그대로 유지
                .build();

        // 업데이트된 엔티티 저장
        meetingMinuteRepository.save(meetingMinute);
    }


    @Transactional
    @Async("asyncExecutor")
    public void updateMeetingMinute(Long id, String title, String summary) {
        // ID로 기존 엔티티 조회
        MeetingMinute meetingMinute = meetingMinuteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MeetingMinute not found with id: " + id));

        // 새로운 값으로 업데이트
        meetingMinute = MeetingMinute.builder()
                .id(meetingMinute.getId()) // 기존 ID 유지
                .title(title != null ? title : meetingMinute.getTitle()) // null 체크 후 업데이트
                .content(meetingMinute.getContent())
                .summary(summary != null ? summary : meetingMinute.getSummary())
                .audioFile(meetingMinute.getAudioFile()) // 오디오 파일도 그대로 유지
                .build();

        // 업데이트된 엔티티 저장
        meetingMinuteRepository.save(meetingMinute);
    }


    public Map<Integer, List<ResponseMeetingDto>> getAllMeetingMinutesByDayOfMonth(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        List<ResponseMeetingDto> meetingDtos = meetingMinuteRepository.findMeetingMinutesOnlyId(year, month, day);

        return meetingDtos.stream()
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate().getDayOfMonth()));
    }

    public MeetingMinute getMeetingMinute(Long id) {
        return meetingMinuteRepository.findById(id).orElseThrow();
    }

    public void deleteMeetingMinute(Long id) {
        meetingMinuteRepository.deleteById(id);
    }
}
