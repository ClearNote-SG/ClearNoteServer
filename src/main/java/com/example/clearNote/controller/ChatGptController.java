package com.example.clearNote.controller;

import com.example.clearNote.dto.ChatGptDto;
import com.example.clearNote.service.ChatGptService;
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
            @RequestParam("meetingText") String meetingText,
            @RequestParam("template") MultipartFile template) {
        // Service로 요청을 넘겨 응답을 반환
        return chatGptService.summarizeMeeting(meetingText, template);
    }
}