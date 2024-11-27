package com.example.clearnote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SttTransmitDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SttResponse {
        private byte[] meetingAudio;
        private String meetingContent;
    }
}
