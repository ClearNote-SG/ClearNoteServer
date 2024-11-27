package com.example.clearnote.application;

import com.example.clearnote.dto.SttTransmitDto;
import org.springframework.stereotype.Service;

@Service
public class SttTransmitService {

    private final MeetingMinuteService meetingMinuteService;
    private final SttService sttService;
    public SttTransmitService(MeetingMinuteService meetingMinuteService, SttService sttService){
        this.meetingMinuteService = meetingMinuteService;
        this.sttService = sttService;
    }

    public SttTransmitDto.SttResponse sttTransmit(Long id) {
        String meetingText;
        byte[] meetingAudio = meetingMinuteService.getMeetingMinute(id).getAudioFile();

        if(meetingMinuteService.getMeetingMinute(id).getContent() == null) {
            meetingText = sttService.transcribe(meetingAudio, id);
            meetingMinuteService.uploadContent(id, meetingText);
        }
        else
            meetingText = meetingMinuteService.getMeetingMinute(id).getContent();

        return new SttTransmitDto.SttResponse(meetingAudio, meetingText);
    }
}
