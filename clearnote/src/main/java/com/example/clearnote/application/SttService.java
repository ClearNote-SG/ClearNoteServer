package com.example.clearnote.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Service
public class SttService {
    private final RestTemplate restTemplate;

    public SttService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String sendAudioToPythonServer(MultipartFile audioFile) {
        String url = "http://localhost:5000/transcribe"; // Python 서버 URL

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            // 파일 데이터를 HttpEntity로 변환
            Resource fileResource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            };
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Python 서버로 POST 요청 보내기
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            // 변환된 텍스트 반환
            return response.getBody();
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process audio file";
        }
    }
}
