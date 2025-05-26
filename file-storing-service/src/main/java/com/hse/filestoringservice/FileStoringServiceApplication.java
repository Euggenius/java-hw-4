package com.hse.filestoringservice;

import com.hse.filestoringservice.config.FileStorageProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
@OpenAPIDefinition(info = @Info(title = "File Storing Service API", version = "v1", description = "API for uploading, managing, and retrieving files."))
public class FileStoringServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileStoringServiceApplication.class, args);
    }
}