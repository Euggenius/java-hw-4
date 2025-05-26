package com.hse.fileanalysisservice.dto;

import com.hse.fileanalysisservice.model.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResultDto {
    private Long id;
    private Long fileId;
    private Integer paragraphCount;
    private Integer wordCount;
    private Integer characterCount;
    private String wordCloudUrl;
    private AnalysisStatus status;
    private String errorMessage;
    private LocalDateTime analysisTimestamp;
    private LocalDateTime lastUpdatedTimestamp;

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

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long fileId;
        private Integer paragraphCount;
        private Integer wordCount;
        private Integer characterCount;
        private String wordCloudUrl;
        private AnalysisStatus status;
        private String errorMessage;
        private LocalDateTime analysisTimestamp;
        private LocalDateTime lastUpdatedTimestamp;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fileId(Long fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder paragraphCount(Integer paragraphCount) {
            this.paragraphCount = paragraphCount;
            return this;
        }

        public Builder wordCount(Integer wordCount) {
            this.wordCount = wordCount;
            return this;
        }

        public Builder characterCount(Integer characterCount) {
            this.characterCount = characterCount;
            return this;
        }

        public Builder wordCloudUrl(String wordCloudUrl) {
            this.wordCloudUrl = wordCloudUrl;
            return this;
        }

        public Builder status(AnalysisStatus status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder analysisTimestamp(LocalDateTime analysisTimestamp) {
            this.analysisTimestamp = analysisTimestamp;
            return this;
        }

        public Builder lastUpdatedTimestamp(LocalDateTime lastUpdatedTimestamp) {
            this.lastUpdatedTimestamp = lastUpdatedTimestamp;
            return this;
        }

        public AnalysisResultDto build() {
            AnalysisResultDto dto = new AnalysisResultDto();
            dto.setId(id);
            dto.setFileId(fileId);
            dto.setParagraphCount(paragraphCount);
            dto.setWordCount(wordCount);
            dto.setCharacterCount(characterCount);
            dto.setWordCloudUrl(wordCloudUrl);
            dto.setStatus(status);
            dto.setErrorMessage(errorMessage);
            dto.setAnalysisTimestamp(analysisTimestamp);
            dto.setLastUpdatedTimestamp(lastUpdatedTimestamp);
            return dto;
        }
    }
}