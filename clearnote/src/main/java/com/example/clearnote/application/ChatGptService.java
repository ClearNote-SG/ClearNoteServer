package com.example.clearnote.application;

import com.example.clearnote.dto.ChatGptDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatGptService {

    private final String model;
    private final String apiURL;
    private final RestTemplate restTemplate;
    //private final String prompt = "아래 텍스트는 회의 음성 녹음을 텍스트로 바꾼 거야. 아래 파일은 해당 회의 내용을 기록할 회의록 양식 pdf 파일이야. 이 회의 내용을 회의록 양식에 있는 내용을 중심으로 요약해줘. 회의에서 나온 정보를 최대한 자세하게 기록해줘.\\n ";
    private final String prompt =
            "###Role###\n" +
            "You are an AI assistant that creates detailed, structured meeting summaries based on specific templates." +
            "I will provide you with a meeting transcript and a meeting template file(as an image)." +
            "Please generate a comprehensive summary that follows the structure of the provided template closely, capturing all important details.\n" +
            "1. Meeting Title: A concise title that captures the main topic or purpose of the meeting.\n" +
            "2. Meeting Summary: A detailed summary based on the provided meeting template, including the main topics, key points discussed, decisions made, and any follow-up actions required." +
                    "Ensure the summary is organized and includes all essential elements outlined in the template.\n" +
            "Please structure your response exactly as follows, with clear delimiters to allow for easy parsing:\n\n" +
            "---\n\n" +
            "**Meeting Title:** [Your Title Here]\n\n" +
            "**Meeting Summary:** [Your Detailed Summary Here, following the template format and covering all critical aspects discussed in the meeting]\n\n" +
            "---\n\n" +
            "Ensure that the meeting summary includes only the relevant information structured according to the provided template.\n" +
            "Here is the meeting transcript:\n[%s]\n\n" +
            "Note: The meeting template will be provided as a separate file. Please base your summary on that template.";


    public ChatGptService(@Value("${openai.model}") String model, @Value("${openai.api.url}") String apiURL, RestTemplate restTemplate){
        this.model = model;
        this.apiURL = apiURL;
        this.restTemplate = restTemplate;
    }

    public ChatGptDto.SummaryResponse summarizeMeeting(String meetingText, MultipartFile template) {

        String finalText = String.format(prompt, meetingText);

        String image = encodeFileToBase64Url(template);
        ChatGptDto.ImageUrl imageUrl = new ChatGptDto.ImageUrl(image, "high");
        ChatGptDto.RequestTextContent textContent = new ChatGptDto.RequestTextContent("text", finalText);
        ChatGptDto.RequestImageContent templateContent = new ChatGptDto.RequestImageContent("image_url", imageUrl);

        ChatGptDto.RequestMessage message = new ChatGptDto.RequestMessage();
        message.setRole("user");
        message.setContent(List.of(textContent, templateContent));

        ChatGptDto.ChatGptRequest chatGptRequest = new ChatGptDto.ChatGptRequest();
        chatGptRequest.setModel(model);
        chatGptRequest.setMessages(List.of(message));

        try {
            // OpenAI API에 POST 요청을 보냄
            ChatGptDto.ChatGptResponse chatGptResponse = restTemplate.postForObject(apiURL, chatGptRequest, ChatGptDto.ChatGptResponse.class);
            // 응답에서 첫 번째 메시지를 추출하여 반환
            String gptResponse = chatGptResponse.getChoices().get(0).getMessage().getContent();
            System.out.println(gptResponse);
            String title = extractSection("Meeting Title:", "**",  gptResponse);
            String summary = extractSection("Meeting Summary:", "---", gptResponse);

            return new ChatGptDto.SummaryResponse(title, summary);
            //return chatGptResponse.getChoices().get(0).getMessage().getContent();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // 응답이 null이거나 비어있을 경우
            return new ChatGptDto.SummaryResponse("요약 요청 중 오류 발생: ", e.getMessage());
        }


    }

    // PDF 파일을 Base64로 인코딩하여 URL 형식으로 반환
    private String encodeFileToBase64Url(MultipartFile file) {
        try {
            String base64Template = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:image/jpeg;base64," + base64Template;
        } catch(IOException e) {
            return "파일 처리 오류: " + e.getMessage();
        }
    }

    public String extractSection(String sectionHeader, String endDelimiter, String text) {
        // 정규 표현식 생성
        String pattern = "\\*\\*" + sectionHeader + "\\*\\*\\s*(.*?)\\s*(?=" + Pattern.quote(endDelimiter) + "|$)";
        Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matcher = regex.matcher(text);

        // 매칭된 부분 반환, 없으면 빈 문자열
        return matcher.find() ? matcher.group(1).trim() : "";
    }
}