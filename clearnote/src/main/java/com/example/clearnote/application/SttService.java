package com.example.clearnote.application;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.Duration;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SttService {


    private final String apiURL;
    private final RestTemplate restTemplate;
    private final Gson gson;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class clovaBoosting {
        private String words;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class clovaDiarization {
        private Boolean enable = Boolean.TRUE;
        private Integer speakerCountMin;
        private Integer speakerCountMax;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class clovaSed {
        private Boolean enable = Boolean.FALSE;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class clovaRequest {
        private String language = "ko-KR";
        //completion optional, sync/async (응답 결과 반환 방식(sync/async) 설정, 필수 파라미터 아님)
        private String completion = "sync";
        //optional, used to receive the analyzed results (분석된 결과 조회 용도, 필수 파라미터 아님)
        private String callback;
        //optional, any data (임의의 Callback URL 값 입력, 필수 파라미터 아님)
        private Map<String, Object> userdata;
        private Boolean wordAlignment = Boolean.TRUE;
        private Boolean fullText = Boolean.TRUE;
        //boosting object array (키워드 부스팅 객체 배열)
        private List<clovaBoosting> boostings;
        //comma separated words (쉼표 구분 키워드)
        private String forbiddens;
        private clovaDiarization diarization;
        private clovaSed sed;
    }

    public SttService(@Value("${clova.api.url}") String apiURL, @Qualifier("clovaTemplate") RestTemplate restTemplate) {
        this.apiURL = apiURL;
        this.restTemplate = restTemplate;
        this.gson = new Gson();
    }


    public String transcribe(byte[] meetingAudio, Long id) {

        clovaRequest clovaRequest = new clovaRequest();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // JSON 데이터를 "params"로 추가
        String jsonParams = gson.toJson(clovaRequest);
        body.add("params", jsonParams);

        File convFile = new File("voice_"+id+".aac");
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(meetingAudio);
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 파일을 "media"로 추가
        FileSystemResource fileResource = new FileSystemResource(convFile);
        body.add("media", fileResource);

        // 요청 본문 설정
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, new HttpHeaders());

        Instant start = Instant.now();

        // RestTemplate을 사용하여 POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                apiURL+ "/recognizer/upload",
                HttpMethod.POST,
                entity,
                String.class
        );

        System.out.println(response.getBody());

        String resultText = new String();
        String jsonResponse = response.getBody();
        JSONObject jsonResult = new JSONObject(jsonResponse);
        String totalText = jsonResult.optString("text");
        JSONArray segments = jsonResult.optJSONArray("segments");
        for (int i = 0; i < segments.length(); i++) {
            JSONObject segment = segments.getJSONObject(i);
            JSONObject speaker = segment.optJSONObject("speaker");
            String speakerLabel = speaker != null ? speaker.optString("label") : "Unknown";
            String text = segment.optString("text");

            resultText = resultText + ("참석자 " + speakerLabel + ": " + text + "\n");
            // 화자별 인식 결과 출력
            System.out.println("참석자 " + speakerLabel + ": " + text);
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("Transcription completed.");
        System.out.println(totalText);
        System.out.println("Elapsed time: " + duration.toMillis() / 1000.0 + " seconds");

        // 응답 결과 반환
        return resultText;
    }
}