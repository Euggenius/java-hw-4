package com.hse.filestoringservice.service;

import com.hse.filestoringservice.dto.FileUploadResponseDto;
import com.hse.filestoringservice.model.FileMetadata;
import com.hse.filestoringservice.repository.FileMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class FileUploadService {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private final HashingService hashingService;
    private final PhysicalStorageService physicalStorageService;
    private final FileMetadataRepository metadataRepository;
    private final AnalysisTriggerService analysisTriggerService;

    @Autowired
    public FileUploadService(HashingService hashingService,
                             PhysicalStorageService physicalStorageService,
                             FileMetadataRepository metadataRepository,
                             AnalysisTriggerService analysisTriggerService) {
        this.hashingService = hashingService;
        this.physicalStorageService = physicalStorageService;
        this.metadataRepository = metadataRepository;
        this.analysisTriggerService = analysisTriggerService;
    }

    @Transactional
    public FileUploadResponseDto processUpload(MultipartFile file) throws IOException {
        String fileHash = hashingService.calculateSha256(file);
        logger.info("Processing file: {}, hash: {}", file.getOriginalFilename(), fileHash);

        Optional<FileMetadata> existingFile = metadataRepository.findByFileHash(fileHash);

        if (existingFile.isPresent()) {
            FileMetadata metadata = existingFile.get();
            logger.info("File with hash {} (original: {}) already exists. ID: {}", fileHash, metadata.getOriginalFileName(), metadata.getId());
            return new FileUploadResponseDto(metadata.getId(), file.getOriginalFilename(), "File already exists (100% match).", true, metadata.getOriginalFileName());
        } else {
            String storageLocation = physicalStorageService.storeFile(file);
            FileMetadata newMetadata = new FileMetadata(
                    file.getOriginalFilename(),
                    fileHash,
                    storageLocation,
                    file.getContentType(),
                    file.getSize()
            );
            FileMetadata savedMetadata = metadataRepository.save(newMetadata);
            logger.info("New file saved. ID: {}, Name: {}, Location: {}", savedMetadata.getId(), savedMetadata.getOriginalFileName(), storageLocation);
            
            analysisTriggerService.triggerAnalysis(savedMetadata.getId(), storageLocation);
            
            return new FileUploadResponseDto(savedMetadata.getId(), savedMetadata.getOriginalFileName(), "File uploaded successfully. Analysis initiated.", false, null);
        }
    }
}