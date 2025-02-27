package com.springboot.club_house_api_server.openai.summary.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class AudioSummaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;

    // mp3 파일의 바이너리 데이터를 저장할 필드 (LONGBLOB)
    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    // 전사 텍스트 (길어질 수 있으므로 LONGTEXT)
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String transcription;

    // 요약 텍스트 (길어질 수 있으므로 LONGTEXT)
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String summary;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    // 기본 생성자
    public AudioSummaryRecord() {
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public byte[] getFileData() {
        return fileData;
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getTranscription() {
        return transcription;
    }
    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
