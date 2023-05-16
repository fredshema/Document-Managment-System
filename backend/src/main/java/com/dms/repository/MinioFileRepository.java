package com.dms.repository;

import com.dms.model.MinioFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MinioFileRepository extends JpaRepository<MinioFile, String> {
    Page<MinioFile> findAllByUserId(Pageable pagable, Long id);
}