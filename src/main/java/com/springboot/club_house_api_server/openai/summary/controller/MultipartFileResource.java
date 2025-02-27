package com.springboot.club_house_api_server.openai.summary.controller;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileResource extends InputStreamResource {

    private final MultipartFile file;

    public MultipartFileResource(MultipartFile file) throws IOException {
        super(file.getInputStream());
        this.file = file;
    }

    @Override
    public String getFilename() {
        return file.getOriginalFilename();
    }
}

