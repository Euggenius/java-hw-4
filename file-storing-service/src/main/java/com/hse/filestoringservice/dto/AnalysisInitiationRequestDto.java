package com.hse.filestoringservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisInitiationRequestDto {
    private Long fileId;
    private String fileLocation;

    public AnalysisInitiationRequestDto(Long fileId, String fileLocation) {
        this.fileId = fileId;
        this.fileLocation = fileLocation;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}