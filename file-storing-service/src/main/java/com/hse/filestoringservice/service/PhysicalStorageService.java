package com.hse.filestoringservice.service;

import com.hse.filestoringservice.config.FileStorageProperties;
import com.hse.filestoringservice.exception.FileStorageException;
import com.hse.filestoringservice.exception.MyFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class PhysicalStorageService {
    private static final Logger logger = LoggerFactory.getLogger(PhysicalStorageService.class);
    private final Path fileStorageLocation;

    @Autowired
    public PhysicalStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
            logger.info("Initialized file storage directory at: {}", this.fileStorageLocation);
            logger.info("Directory exists: {}", Files.exists(this.fileStorageLocation));
            logger.info("Directory is writable: {}", Files.isWritable(this.fileStorageLocation));
        } catch (Exception ex) {
            logger.error("Failed to initialize storage directory: {}", this.fileStorageLocation, ex);
            throw new FileStorageException("Could not create the directory for uploaded files: " + this.fileStorageLocation, ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("Storing file: {}", originalFileName);
        logger.info("Storage location: {}", this.fileStorageLocation);
        
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + originalFileName);
        }
        
        String uniqueSubdirectory = UUID.randomUUID().toString().substring(0, 2);
        Path uniqueDir = this.fileStorageLocation.resolve(uniqueSubdirectory);
        logger.info("Creating subdirectory: {}", uniqueDir);
        
        try {
            Files.createDirectories(uniqueDir);
            logger.info("Subdirectory created successfully");
        } catch (IOException ex) {
            logger.error("Failed to create subdirectory: {}", uniqueDir, ex);
            throw new FileStorageException("Could not create subdirectory for file storage.", ex);
        }

        String uniqueFileNameSuffix = UUID.randomUUID().toString();
        String targetFileName = uniqueFileNameSuffix + "_" + originalFileName;
        Path targetLocation = uniqueDir.resolve(targetFileName);
        logger.info("Target file location: {}", targetLocation);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Successfully stored file {} at {}", originalFileName, targetLocation);
            return targetLocation.toString();
        } catch (IOException ex) {
            logger.error("Failed to store file: {}", originalFileName, ex);
            throw new FileStorageException("Could not store file " + originalFileName, ex);
        }
    }

    public Resource loadFileAsResource(String filePathString) {
        try {
            Path filePath = Paths.get(filePathString).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                logger.error("File not found or not readable at path: {}", filePathString);
                throw new MyFileNotFoundException("File not found: " + filePathString);
            }
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL for path: {}", filePathString, ex);
            throw new MyFileNotFoundException("File not found (malformed URL): " + filePathString, ex);
        }
    }
}