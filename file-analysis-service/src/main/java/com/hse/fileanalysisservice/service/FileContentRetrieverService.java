package com.hse.fileanalysisservice.service;

import com.hse.fileanalysisservice.exception.RemoteServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.StandardCharsets;

@Service
public class FileContentRetrieverService {
    private static final Logger logger = LoggerFactory.getLogger(FileContentRetrieverService.class);
    private final RestTemplate restTemplate;
    private final String fileStoringServiceBaseUrl;

    public FileContentRetrieverService(RestTemplate restTemplate, @Value("${file-storing-service.url}") String fileStoringServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.fileStoringServiceBaseUrl = fileStoringServiceBaseUrl;
    }

    public String getFileContentById(Long fileId) {
        String url = fileStoringServiceBaseUrl + "/" + fileId + "/content";
        logger.info("Requesting file content from: {}", url);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return new String(response.getBody(), StandardCharsets.UTF_8);
            } else {
                logger.error("Failed to retrieve file content for id {}. Status: {}", fileId, response.getStatusCode());
                throw new RemoteServiceException("Failed to retrieve file content. Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            logger.error("Client error fetching file content for id {}: {} - {}", fileId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RemoteServiceException("Client error fetching file from " + url + ": " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            logger.error("Error connecting to File Storing Service at {} for id {}: {}", url, fileId, e.getMessage(), e);
            throw new RemoteServiceException("File Storing Service unavailable at " + url, e);
        } catch (Exception e) {
            logger.error("Unexpected error fetching file content for id {}: {}", fileId, e.getMessage(), e);
            throw new RemoteServiceException("Unexpected error fetching file from " + url, e);
        }
    }
}