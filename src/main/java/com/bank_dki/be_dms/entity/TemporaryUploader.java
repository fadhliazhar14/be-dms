package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "temporaryuploader")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryUploader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TemporaryUploaderId")
    private Short temporaryUploaderId;
    
    @Lob
    @Column(name = "TemporaryUploaderBlobFile")
    private byte[] temporaryUploaderBlobFile;
    
    @Column(name = "TemporaryUploaderBlobFile_GXI", length = 2048)
    private String temporaryUploaderBlobFileGxi;
    
    @Column(name = "TemporaryUploaderCreatedDateTi")
    private LocalDateTime temporaryUploaderCreatedDateTi;
    
    @PrePersist
    protected void onCreate() {
        temporaryUploaderCreatedDateTi = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemporaryUploader that = (TemporaryUploader) o;
        return Objects.equals(temporaryUploaderId, that.temporaryUploaderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(temporaryUploaderId);
    }
}