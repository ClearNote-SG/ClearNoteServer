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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingMinuteService {
    private final MeetingMinuteRepository meetingMinuteRepository;

    @Transactional
    public MeetingMinute upload(MultipartFile voiceFile) throws IOException {
        // TODO: whisper api를 이용하여 음성 파일을 텍스트로 변환
        String content = null;
        // TODO: 변환된 텍스트를 ChatGpt api를 이용하여 제목과 요약본 생성
        String title = null;
        String summary = null;

        return meetingMinuteRepository.save(MeetingMinute.builder()
                .title(title)
                .content(content)
                .summary(summary).audioFile(voiceFile.getBytes())
                .build());
    }

    public Map<Integer, List<ResponseMeetingDto>> getAllMeetingMinutesByDayOfMonth(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        List<ResponseMeetingDto> meetingDtos = meetingMinuteRepository.findMeetingMinutesOnlyId(year, month);

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
