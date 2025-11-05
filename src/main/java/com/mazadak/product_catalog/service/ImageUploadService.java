package com.mazadak.product_catalog.service;

import io.nexusrpc.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUploadService {
    String uploadFile(MultipartFile file) throws IOException;
}
