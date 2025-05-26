package com.hse.filestoringservice.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class HashingService {
    private static final Logger logger = LoggerFactory.getLogger(HashingService.class);

    public String calculateSha256(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            logger.info("File content for {}: [{}]", file.getOriginalFilename(), content);
            
            String hash = DigestUtils.sha256Hex(content.getBytes(StandardCharsets.UTF_8));
            logger.info("Calculated hash for file {}: {}", file.getOriginalFilename(), hash);
            return hash;
        }
    }
}