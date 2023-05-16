package com.dms.service;

import com.dms.dto.FileDto;
import com.dms.model.MinioFile;
import com.dms.model.User;
import com.dms.repository.MinioFileRepository;
import com.dms.repository.UserRepository;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioFileRepository minioFileRepository;

    @Value("${minio.bucket.name}")
    private String bucketName;
    @Autowired
    private UserRepository userRepository;

    public String getFilename(String id) {
        MinioFile file = minioFileRepository.findById(id).orElse(null);
        if (file == null) {
            return "unknown file";
        }

        String extension = file.getFilename().substring(file.getFilename().lastIndexOf(".") + 1);
        return file.getTitle() + "." + extension;
    }

    public Page<MinioFile> getListObjects(Integer page, Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUsername(authentication.getName());
        return minioFileRepository.findAllByUserId(PageRequest.of(page, size), user.getId());
    }

    public MinioFile uploadFile(FileDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUsername(authentication.getName());
        ObjectWriteResponse item = null;
        System.out.println("Username: " + authentication.getName());
        try {
            item = minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(request.getFile().getOriginalFilename()).stream(request.getFile().getInputStream(), request.getFile().getSize(), -1).build());
        } catch (Exception e) {
            log.error("Happened error when upload file: " + e);
        }

        MinioFile minioFile = new MinioFile();
        minioFile.setId(item != null ? item.etag() : null);
        minioFile.setTitle(request.getTitle());
        minioFile.setSize(request.getFile().getSize());
        minioFile.setFilename(request.getFile().getOriginalFilename());
        minioFile.setUser(user);

        minioFileRepository.save(minioFile);

        return minioFile;
    }

    public InputStream getObject(String id) {
        InputStream stream;
        MinioFile minioFile = minioFileRepository.findById(id).orElse(null);
        if (minioFile == null) {
            log.error("Happened error when get object from minio: " + "Object not found");
            return null;
        }
        try {
            stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(minioFile.getFilename()).build());
        } catch (Exception e) {
            log.error("Happened error when get list objects from minio: " + e);
            return null;
        }

        return stream;
    }

    public void deleteObject(String id) {
        try {
            MinioFile minioFile = minioFileRepository.findById(id).orElse(null);
            if (minioFile == null) {
                log.error("Happened error when delete object from minio: " + "Object not found");
                return;
            }

            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(minioFile.getFilename()).build());
            minioFileRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Happened error when delete object from minio: " + e);
        }
    }

    public void updateObject(String id, String newName) {
        try {
            MinioFile minioFile = minioFileRepository.findById(id).orElse(null);

            if (minioFile == null) {
                log.error("Happened error when update object from minio: " + "Object not found");
                return;
            }

            String extension = minioFile.getFilename().substring(minioFile.getFilename().lastIndexOf('.'));
            newName = newName.concat(extension);

            minioClient.copyObject(CopyObjectArgs.builder().bucket(bucketName).object(newName).source(CopySource.builder().bucket(bucketName).object(minioFile.getFilename()).build()).build());

            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(minioFile.getFilename()).build());

            minioFile.setFilename(newName);
            minioFileRepository.save(minioFile);

        } catch (Exception e) {
            log.error("Happened error when update object from minio: " + e);
        }
    }

    public List<MinioFile> searchObject(String filename) {
        List<MinioFile> objects = new ArrayList<>();
        try {
            Iterable<Result<Item>> result = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
            for (Result<Item> item : result) {
                if (item.get().objectName().contains(filename)) {
                    objects.add(MinioFile.builder().id(item.get().etag()).title(item.get().objectName()).filename(item.get().objectName()).size(item.get().size()).build());
                }
            }
            return objects;
        } catch (Exception e) {
            log.error("Happened error when get list objects from minio: " + e);
        }

        return objects;
    }

    public void sync() {
        try {
            Iterable<Result<Item>> result = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
            List<MinioFile> minioFiles = minioFileRepository.findAll();

            List<Item> cloudFiles = new ArrayList<>();

            for (Result<Item> item : result) {
                cloudFiles.add(item.get());
            }

            List<MinioFile> deletedFiles = minioFiles.stream()
                    .filter(minioFile -> cloudFiles.stream()
                            .noneMatch(item -> item.etag()
                                    .equals(minioFile.getId()))).toList();

            List<MinioFile> newFiles = cloudFiles.stream()
                    .filter(item -> minioFiles.stream()
                            .noneMatch(minioFile -> minioFile.getId()
                                    .equals(item.etag())))
                    .map(item -> MinioFile.builder().id(item.etag())
                            .title(item.objectName())
                            .filename(item.objectName())
                            .size(item.size())
                            .user(null)
                            .build()).toList();

            minioFileRepository.deleteAll(deletedFiles);
            minioFileRepository.saveAll(newFiles);
        } catch (Exception e) {
            log.error("Happened error while syncing objects from minio: " + e);
        }
    }
}
