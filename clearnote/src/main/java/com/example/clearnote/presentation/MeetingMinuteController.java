package com.example.clearnote.presentation;

import com.example.clearnote.application.MeetingMinuteService;
import com.example.clearnote.dto.ResponseMeetingDto;
import com.example.clearnote.entity.MeetingMinute;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting")
public class MeetingMinuteController {
    private final MeetingMinuteService meetingMinuteService;

    @PostMapping
    public ResponseEntity<Long> addMeetingMinute(@RequestBody MultipartFile voiceFile) {
        Long id = meetingMinuteService.createMeetingMinute();
        byte[] voiceByte = new byte[0];
        try {
            voiceByte = voiceFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        meetingMinuteService.uploadVoice(id, voiceByte);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/meetingByDate/{date}")
    public ResponseEntity<Map<Integer, List<ResponseMeetingDto>>> getAllMeetingMinutesByDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(meetingMinuteService.getAllMeetingMinutesByDayOfMonth(date));
    }

    @GetMapping("/meetingById/{id}")
    public ResponseEntity<MeetingMinute> getMeetingMinute(@PathVariable Long id) {
        return ResponseEntity.ok(meetingMinuteService.getMeetingMinute(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMeetingMinute(@PathVariable Long id) {
        meetingMinuteService.deleteMeetingMinute(id);
        return ResponseEntity.ok("Meeting minute deleted successfully");
    }
}
