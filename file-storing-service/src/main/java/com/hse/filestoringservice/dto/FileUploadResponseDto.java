package com.hse.filestoringservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDto {
    private Long fileId;
    private String fileName;
    private String message;
    private boolean isDuplicate;
    private String existingFileOriginalName;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public String getExistingFileOriginalName() {
        return existingFileOriginalName;
    }

    public void setExistingFileOriginalName(String existingFileOriginalName) {
        this.existingFileOriginalName = existingFileOriginalName;
    }
}