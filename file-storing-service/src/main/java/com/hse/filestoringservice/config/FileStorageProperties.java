package com.hse.filestoringservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
@Validated
public class FileStorageProperties {
    @NotBlank
    private String uploadDir = "./filestore_data";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}