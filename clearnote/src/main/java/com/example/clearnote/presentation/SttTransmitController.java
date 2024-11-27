package com.example.clearnote.presentation;

import com.example.clearnote.application.SttTransmitService;
import com.example.clearnote.dto.SttTransmitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SttTransmitController {

    private final SttTransmitService sttTransmitService;

    @PostMapping("/stt")
    public SttTransmitDto.SttResponse summarizeMeeting(
            @RequestParam("meetingId") Long id) {

        return sttTransmitService.sttTransmit(id);
    }
}
