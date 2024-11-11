package com.example.clearNote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
public class ChatGptDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageUrl {

        private String url;
        private String detail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestTextContent {
        private String type;       // "text" 또는 "image_url"
        private String text;        // 텍스트 데이터
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestImageContent {
        private String type;       // "text" 또는 "image_url"
        private ImageUrl image_url;   // 이미지 URL 데이터 (Base64 인코딩 포함)
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestMessage {
        private String role;
        private List<Object> content;
    }

    @Data
    public static class ChatGptRequest {
        private String model;
        private List<RequestMessage> messages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseMessage {
        private String role;
        private String content;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;
        private ResponseMessage message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatGptResponse {
        private List<Choice> choices;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private String meetingTitle;
        private String meetingSummary;
    }
}