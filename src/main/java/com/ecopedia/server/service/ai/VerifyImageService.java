package com.ecopedia.server.service.ai;

import com.ecopedia.server.web.dto.ai.VerifyImageReturnDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VerifyImageService {
    private static final String OPENAI_API_KEY = "sk-proj-UhXobRwk2HqhgaUEaLG26CoZarmX_LPsjMpTxwG9OaBSPOm0be-Y5DH14Wbjp9epEOEDL6eM5XT3BlbkFJSPEG8rCSr6VKymkFUW6SclJH4V8ndlwGL4FM4j6VLJ3DsU78iWXAFF_MSnY0jnyjTktJ3OZnQ";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_MODEL = "gpt-4.1/Vision";
    private static final String OPENAI_PROMPT = "이 이미지의 사진이 식물, 곤충 이라면 { \"category\": \"식물\" or \"곤충\", name: \"식물이나 곤충의 한국어 이름\", description: \"해당 식물, 곤충의 설명\" } 정보를 리턴해주고 만약 식물, 곤충이 아니라면 { \"category\": \"\", name: \"\", description: \"\" } 형태를 리턴해줘";
    private static final RestTemplate restTemplate = new RestTemplate();

    public VerifyImageReturnDto verifyImage(String cloudImageUrl) {
        // JSON 동적 생성
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", OPENAI_PROMPT);

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        imageUrl.put("url", cloudImageUrl);
        imageContent.put("image_url", imageUrl);

        List<Object> messageContent = Arrays.asList(textContent, imageContent);
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", messageContent);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", OPENAI_MODEL);
        requestBody.put("messages", List.of(message));

        // HTTP 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(OPENAI_API_KEY + "A");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_API_URL, HttpMethod.POST, entity, String.class);

        System.out.println("response.getBody() >>> " + response.getBody());

        String body = response.getBody();

        VerifyImageReturnDto returnDto = new VerifyImageReturnDto();

        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1차 파싱: 최상위 응답 전체 파싱
            JsonNode root = mapper.readTree(body);

            // 'content'가 위치한 경로로 접근
            JsonNode contentNode = root.at("/choices/0/message/content");
            if (contentNode.isMissingNode()) {
                throw new RuntimeException("content 필드를 찾을 수 없습니다.");
            }

            // contentNode는 JSON 문자열이 이스케이프된 상태의 텍스트임
            String innerJsonString = contentNode.asText();

            // 2차 파싱: content 문자열(JSON 포맷)을 다시 JSON으로 파싱
            JsonNode contentJson = mapper.readTree(innerJsonString);

            String category = contentJson.get("category").asText();
            String name = contentJson.get("name").asText();
            String description = contentJson.get("description").asText();

            returnDto.setCategory(category);
            returnDto.setName(name);
            returnDto.setDescription(description);

            return returnDto;
        } catch (Exception e) {
            e.printStackTrace();
        }

        returnDto.setCategory("");
        returnDto.setName("");
        returnDto.setDescription("");
        return returnDto;
    }
}
