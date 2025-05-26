package com.hse.fileanalysisservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@OpenAPIDefinition(info = @Info(title = "File Analysis Service API", version = "v1", description = "API for analyzing files and retrieving results."))
public class FileAnalysisServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileAnalysisServiceApplication.class, args);
    }
}