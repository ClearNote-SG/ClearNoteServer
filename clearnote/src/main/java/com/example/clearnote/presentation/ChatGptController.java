package com.example.clearnote.presentation;

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

    @PostMapping("/summarize")
    public ChatGptDto.SummaryResponse summarizeMeeting(
            @RequestParam("meetingAudio") MultipartFile meetingAudio,
            @RequestParam("meetingTemplate") MultipartFile meetingTemplate) {
        // Service로 요청을 넘겨 응답을 반환
        return chatGptService.summarizeMeeting(meetingAudio, meetingTemplate);
    }
}