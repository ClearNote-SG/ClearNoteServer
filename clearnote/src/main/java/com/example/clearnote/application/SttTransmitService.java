package com.example.clearnote.application;

import com.example.clearnote.dto.ChatGptDto;
import com.example.clearnote.dto.SttTransmitDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SttTransmitService {

    /*(public ChatGptService(@Value("${openai.model}") String model, @Value("${openai.api.url}") String apiURL, @Qualifier("openAiTemplate") RestTemplate restTemplate, SttService sttService, MeetingMinuteService meetingMinuteService){
        this.model = model;
        this.apiURL = apiURL;
        this.restTemplate = restTemplate;
        this.sttService = sttService;
        this.meetingMinuteService = meetingMinuteService;
    }*/
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
