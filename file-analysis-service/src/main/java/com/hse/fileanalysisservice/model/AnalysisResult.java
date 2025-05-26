package com.hse.fileanalysisservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results", indexes = {
    @Index(name = "idx_analysisresult_fileid", columnList = "fileId", unique = true)
})
@Data
@NoArgsConstructor
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long fileId;

    private Integer paragraphCount;
    private Integer wordCount;
    private Integer characterCount;

    @Column(length = 1024)
    private String wordCloudUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column(length = 512)
    private String errorMessage;

    private LocalDateTime createdTimestamp;
    private LocalDateTime analysisTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

    public AnalysisResult(Long fileId) {
        this.fileId = fileId;
        this.status = AnalysisStatus.PENDING;
        this.createdTimestamp = LocalDateTime.now();
        this.lastUpdatedTimestamp = LocalDateTime.now();
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Integer getParagraphCount() {
        return paragraphCount;
    }

    public void setParagraphCount(Integer paragraphCount) {
        this.paragraphCount = paragraphCount;
    }

    public Integer getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(Integer characterCount) {
        this.characterCount = characterCount;
    }

    public String getWordCloudUrl() {
        return wordCloudUrl;
    }

    public void setWordCloudUrl(String wordCloudUrl) {
        this.wordCloudUrl = wordCloudUrl;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public LocalDateTime getAnalysisTimestamp() {
        return analysisTimestamp;
    }

    public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) {
        this.analysisTimestamp = analysisTimestamp;
    }

    public LocalDateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(LocalDateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }
}