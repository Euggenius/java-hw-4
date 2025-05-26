package com.hse.fileanalysisservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hse.fileanalysisservice.exception.RemoteServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WordCloudClientService {
    private static final Logger logger = LoggerFactory.getLogger(WordCloudClientService.class);
    private final RestTemplate restTemplate;
    private final String wordCloudApiUrl;
    private final ObjectMapper objectMapper;

    public WordCloudClientService(RestTemplate restTemplate,
                                  @Value("${quickchart.wordcloud-api-url}") String wordCloudApiUrl,
                                  ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.wordCloudApiUrl = wordCloudApiUrl;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public String generateWordCloudUrl(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("text", text);
            requestBodyMap.put("format", "png");
            requestBodyMap.put("width", 600);
            requestBodyMap.put("height", 400);
            requestBodyMap.put("fontFamily", "sans-serif"); 
            requestBodyMap.put("fontScale", 15);
            requestBodyMap.put("rotation", 0); 

            String requestJson = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            
            String apiUrlWithGetUrl = wordCloudApiUrl + "?geturl=true&encoding=base64";
            
            logger.debug("Requesting word cloud from: {} with body: {}", apiUrlWithGetUrl, requestJson);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrlWithGetUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), HashMap.class);
                if (Boolean.TRUE.equals(responseMap.get("success")) && responseMap.containsKey("url")) {
                    String url = (String) responseMap.get("url");
                    logger.info("Word cloud URL received: {}", url);
                    return url;
                } else {
                     logger.error("Word cloud API response error. Success: {}, Body: {}", responseMap.get("success"), response.getBody());
                     return null;
                }
            } else {
                logger.error("Failed to generate word cloud from API. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (RestClientException e) {
            logger.error("RestClientException calling Word Cloud API: {}", e.getMessage(), e);
            throw new RemoteServiceException("Error communicating with Word Cloud API: " + e.getMessage(), e);
        }
         catch (Exception e) {
            logger.error("Generic exception calling Word Cloud API: {}", e.getMessage(), e);
            throw new RemoteServiceException("Unexpected error generating word cloud: " + e.getMessage(), e);
        }
    }
}