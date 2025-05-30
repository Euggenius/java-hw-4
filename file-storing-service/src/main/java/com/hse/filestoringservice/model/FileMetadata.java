package com.hse.filestoringservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata", indexes = {
    @Index(name = "idx_filemetadata_hash", columnList = "fileHash", unique = true)
})
@Data
@NoArgsConstructor
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true, length = 64)
    private String fileHash;

    @Column(nullable = false, length = 512)
    private String storageLocation;

    @Column(nullable = false)
    private String contentType;

    private long size;

    @Column(nullable = false)
    private LocalDateTime uploadTimestamp;

    public FileMetadata(String originalFileName, String fileHash, String storageLocation, String contentType, long size) {
        this.originalFileName = originalFileName;
        this.fileHash = fileHash;
        this.storageLocation = storageLocation;
        this.contentType = contentType;
        this.size = size;
        this.uploadTimestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(LocalDateTime uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }
}