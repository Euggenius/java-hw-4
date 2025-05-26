package com.hse.fileanalysisservice.controller;

import com.hse.fileanalysisservice.dto.AnalysisRequestDto;
import com.hse.fileanalysisservice.dto.AnalysisResultDto;
import com.hse.fileanalysisservice.model.AnalysisResult;
import com.hse.fileanalysisservice.service.AnalysisOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);
    private final AnalysisOrchestrationService analysisOrchestrationService;

    @Autowired
    public AnalysisController(AnalysisOrchestrationService analysisOrchestrationService) {
        this.analysisOrchestrationService = analysisOrchestrationService;
    }

    @Operation(summary = "Initiate file analysis", responses = {
        @ApiResponse(responseCode = "202", description = "Analysis initiated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnalysisResultDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/initiate")
    public ResponseEntity<AnalysisResultDto> initiateAnalysis(@Valid @RequestBody AnalysisRequestDto requestDto) {
        logger.info("Received request to initiate analysis for fileId: {}", requestDto.getFileId());
        try {
            AnalysisResult result = analysisOrchestrationService.initiateAnalysis(requestDto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapToDto(result));
        } catch (Exception e) {
            logger.error("Error initiating analysis for fileId {}: {}", requestDto.getFileId(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initiate analysis", e);
        }
    }

    @Operation(summary = "Get analysis result by file ID", responses = {
        @ApiResponse(responseCode = "200", description = "Analysis result found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnalysisResultDto.class))),
        @ApiResponse(responseCode = "404", description = "Analysis not found")
    })
    @GetMapping("/{fileId}")
    public ResponseEntity<AnalysisResultDto> getAnalysisResult(@Parameter(description = "ID of the file", required = true) @PathVariable Long fileId) {
        logger.debug("Request for analysis result for fileId: {}", fileId);
        return analysisOrchestrationService.getAnalysisResultByFileId(fileId)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis result not found for file ID: " + fileId));
    }

    private AnalysisResultDto mapToDto(AnalysisResult result) {
        if (result == null) return null;
        return AnalysisResultDto.builder()
                .id(result.getId())
                .fileId(result.getFileId())
                .paragraphCount(result.getParagraphCount())
                .wordCount(result.getWordCount())
                .characterCount(result.getCharacterCount())
                .wordCloudUrl(result.getWordCloudUrl())
                .status(result.getStatus())
                .errorMessage(result.getErrorMessage())
                .analysisTimestamp(result.getAnalysisTimestamp())
                .lastUpdatedTimestamp(result.getLastUpdatedTimestamp())
                .build();
    }
}