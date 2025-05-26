package com.hse.filestoringservice.service;

import com.hse.filestoringservice.dto.AnalysisInitiationRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class AnalysisTriggerService {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisTriggerService.class);
    private final RestTemplate restTemplate;
    private final String analysisServiceUrl;

    public AnalysisTriggerService(RestTemplate restTemplate, @Value("${analysis-service.url}") String analysisServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.analysisServiceUrl = analysisServiceBaseUrl + "/initiate";
    }

    public void triggerAnalysis(Long fileId, String fileLocation) {
        AnalysisInitiationRequestDto requestDto = new AnalysisInitiationRequestDto(fileId, fileLocation);
        try {
            logger.info("Triggering analysis for fileId: {} at location: {}", fileId, fileLocation);
            ResponseEntity<Void> response = restTemplate.postForEntity(analysisServiceUrl, requestDto, Void.class);
            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                logger.info("Analysis initiation accepted for fileId: {}", fileId);
            } else {
                logger.warn("Analysis initiation for fileId: {} returned status: {}", fileId, response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            logger.error("Client error triggering analysis for fileId: {}. Status: {}, Response: {}", fileId, e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            logger.error("Error connecting to Analysis Service (url: {}) for fileId: {}. Message: {}", analysisServiceUrl, fileId, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error triggering analysis for fileId: {}", fileId, e);
        }
    }
}