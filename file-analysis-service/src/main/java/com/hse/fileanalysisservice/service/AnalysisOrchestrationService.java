package com.hse.fileanalysisservice.service;

import com.hse.fileanalysisservice.dto.AnalysisRequestDto;
import com.hse.fileanalysisservice.exception.AnalysisException;
import com.hse.fileanalysisservice.model.AnalysisResult;
import com.hse.fileanalysisservice.model.AnalysisStatus;
import com.hse.fileanalysisservice.repository.AnalysisResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AnalysisOrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisOrchestrationService.class);

    private final AnalysisResultRepository analysisResultRepository;
    private final FileContentRetrieverService fileContentRetrieverService;
    private final TextProcessingService textProcessingService;
    private final WordCloudClientService wordCloudClientService;

    @Autowired
    public AnalysisOrchestrationService(AnalysisResultRepository analysisResultRepository,
                                        FileContentRetrieverService fileContentRetrieverService,
                                        TextProcessingService textProcessingService,
                                        WordCloudClientService wordCloudClientService) {
        this.analysisResultRepository = analysisResultRepository;
        this.fileContentRetrieverService = fileContentRetrieverService;
        this.textProcessingService = textProcessingService;
        this.wordCloudClientService = wordCloudClientService;
    }

    @Transactional
    public AnalysisResult initiateAnalysis(AnalysisRequestDto requestDto) {
        logger.info("Initiating analysis for fileId: {}", requestDto.getFileId());
        AnalysisResult analysisResult = analysisResultRepository.findByFileId(requestDto.getFileId())
            .orElseGet(() -> {
                AnalysisResult newResult = new AnalysisResult(requestDto.getFileId());
                return analysisResultRepository.save(newResult);
            });

        if (analysisResult.getStatus() == AnalysisStatus.COMPLETED || analysisResult.getStatus() == AnalysisStatus.PROCESSING) {
            logger.warn("Analysis for fileId {} already {} or completed. Returning existing status.", requestDto.getFileId(), analysisResult.getStatus());
            return analysisResult;
        }
        
        analysisResult.setStatus(AnalysisStatus.PENDING);
        analysisResult.setErrorMessage(null);
        analysisResult.setParagraphCount(null);
        analysisResult.setWordCount(null);
        analysisResult.setCharacterCount(null);
        analysisResult.setWordCloudUrl(null);
        analysisResult.setLastUpdatedTimestamp(LocalDateTime.now());
        AnalysisResult savedResult = analysisResultRepository.save(analysisResult);
        
        performActualAnalysisAsync(savedResult.getId(), requestDto.getFileId(), requestDto.getFileLocation());
        return savedResult;
    }
    
    @Async("taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void performActualAnalysisAsync(Long analysisRecordId, Long fileId, String fileLocation) {
        logger.info("Starting async processing for analysis record ID: {}, file ID: {}", analysisRecordId, fileId);
        Optional<AnalysisResult> arOptional = analysisResultRepository.findById(analysisRecordId);

        if (arOptional.isEmpty()) {
             logger.error("Analysis record ID {} not found for async processing of fileId {}.", analysisRecordId, fileId);
             return;
        }
        AnalysisResult analysisResult = arOptional.get();

        try {
            analysisResult.setStatus(AnalysisStatus.PROCESSING);
            analysisResult.setLastUpdatedTimestamp(LocalDateTime.now());
            analysisResultRepository.saveAndFlush(analysisResult);

            String fileContent = fileContentRetrieverService.getFileContentById(fileId);
            textProcessingService.calculateStatistics(analysisResult, fileContent);
            
            if (wordCloudClientService != null) {
                 String textForCloud = textProcessingService.getTextForWordCloud(fileContent);
                 if (textForCloud != null && !textForCloud.trim().isEmpty()) {
                    String cloudUrl = wordCloudClientService.generateWordCloudUrl(textForCloud);
                    analysisResult.setWordCloudUrl(cloudUrl);
                }
            }
            analysisResult.setStatus(AnalysisStatus.COMPLETED);
            analysisResult.setErrorMessage(null);
            logger.info("Analysis COMPLETED for fileId: {}", fileId);
        } catch (Exception e) {
            logger.error("Analysis FAILED for fileId: {}. Error: {}", fileId, e.getMessage(), e);
            analysisResult.setStatus(AnalysisStatus.FAILED);
            analysisResult.setErrorMessage(e.getMessage().substring(0, Math.min(e.getMessage().length(), 500)));
        } finally {
            analysisResult.setAnalysisTimestamp(LocalDateTime.now());
            analysisResult.setLastUpdatedTimestamp(LocalDateTime.now());
            analysisResultRepository.saveAndFlush(analysisResult);
        }
    }

    public Optional<AnalysisResult> getAnalysisResultByFileId(Long fileId) {
        return analysisResultRepository.findByFileId(fileId);
    }
}