package com.dms.repository;

import com.dms.model.MinioFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MinioFileRepository extends JpaRepository<MinioFile, String> {
    List<MinioFile> findAllByUserId(Long user_id);
}
