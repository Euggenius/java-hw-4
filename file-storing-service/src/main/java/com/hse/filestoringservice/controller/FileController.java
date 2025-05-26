package com.hse.filestoringservice.controller;

import com.hse.filestoringservice.dto.FileUploadResponseDto;
import com.hse.filestoringservice.exception.MyFileNotFoundException;
import com.hse.filestoringservice.model.FileMetadata;
import com.hse.filestoringservice.repository.FileMetadataRepository;
import com.hse.filestoringservice.service.FileUploadService;
import com.hse.filestoringservice.service.PhysicalStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileUploadService fileUploadService;
    private final FileMetadataRepository metadataRepository;
    private final PhysicalStorageService physicalStorageService;

    @Autowired
    public FileController(FileUploadService fileUploadService,
                          FileMetadataRepository metadataRepository,
                          PhysicalStorageService physicalStorageService) {
        this.fileUploadService = fileUploadService;
        this.metadataRepository = metadataRepository;
        this.physicalStorageService = physicalStorageService;
    }

    @Operation(summary = "Upload a .txt file", responses = {
        @ApiResponse(responseCode = "200", description = "File processed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileUploadResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(@Parameter(description = ".txt file to upload", required = true) @RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".txt")) {
            return ResponseEntity.badRequest().body(new FileUploadResponseDto(null, file.getOriginalFilename(), "Invalid file. Please upload a non-empty .txt file.", false, null));
        }
        try {
            FileUploadResponseDto response = fileUploadService.processUpload(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Could not process file upload for {}", file.getOriginalFilename(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process file: " + e.getMessage(), e);
        }
    }

    @Operation(summary = "Get file metadata by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Metadata found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileMetadata.class))),
        @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FileMetadata> getFileMetadata(@Parameter(description = "ID of the file", required = true) @PathVariable Long id) {
        return metadataRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MyFileNotFoundException("File metadata not found with id " + id));
    }

    @Operation(summary = "Get file content by ID", responses = {
        @ApiResponse(responseCode = "200", description = "File content", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getFileContent(@Parameter(description = "ID of the file", required = true) @PathVariable Long id) {
        FileMetadata metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new MyFileNotFoundException("File metadata not found with id " + id));
        
        Resource resource = physicalStorageService.loadFileAsResource(metadata.getStorageLocation());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }
}