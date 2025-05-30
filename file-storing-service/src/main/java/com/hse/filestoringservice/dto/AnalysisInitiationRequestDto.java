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

    public Long getFileId() {
        return fileId;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}