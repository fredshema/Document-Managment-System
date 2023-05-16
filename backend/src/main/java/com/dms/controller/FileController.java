package com.dms.controller;

import com.dms.dto.FileDto;
import com.dms.model.MinioFile;
import com.dms.service.MinioService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class FileController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<Object> fileUpload(@ModelAttribute FileDto request) throws Exception {
        return new ResponseEntity<>(minioService.uploadFile(request), HttpStatus.OK);
    }

    @GetMapping(path = "/download/{id}")
    public ResponseEntity<Object> download(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + minioService.getFilename(id) + "\"")
                .body(IOUtils.toByteArray(minioService.getObject(id)));
    }

    @GetMapping("/files")
    public ResponseEntity<Page<MinioFile>> getListOfFiles(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) throws Exception {
        return new ResponseEntity<>(minioService.getListObjects(page, size), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") String id) throws Exception {
        minioService.deleteObject(id);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateFile(@PathVariable("id") String id, @RequestBody Map body) throws Exception {
        minioService.updateObject(id, body.get("name").toString());
        return new ResponseEntity<>("Successfully updated", HttpStatus.OK);
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<List<MinioFile>> searchFile(@PathVariable("id") String id) throws Exception {
        return new ResponseEntity<>(minioService.searchObject(id), HttpStatus.OK);
    }

    @GetMapping("/sync")
    public ResponseEntity<Object> sync() throws Exception {
        minioService.sync();
        return new ResponseEntity<>("Successfully synced", HttpStatus.OK);
    }
}
