package com.example.clearnote.presentation;

import com.example.clearnote.application.MeetingMinuteService;
import com.example.clearnote.dto.ChatGptDto;
import com.example.clearnote.application.ChatGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ChatGptController {

    //@Autowired
    private final ChatGptService chatGptService;
    private final MeetingMinuteService meetingMinuteService;

    @PostMapping("/summarize")
    public ChatGptDto.SummaryResponse summarizeMeeting(
            @RequestParam("meetingId") Long id,
            @RequestParam("meetingTemplate") MultipartFile meetingTemplate) {
        byte[] meetingAudio = meetingMinuteService.getMeetingMinute(id).getAudioFile();
        return chatGptService.summarizeMeeting(id, meetingAudio, meetingTemplate);
    }
}