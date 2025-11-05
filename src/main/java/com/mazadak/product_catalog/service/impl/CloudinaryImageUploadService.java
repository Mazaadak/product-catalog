package com.mazadak.product_catalog.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mazadak.product_catalog.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryImageUploadService implements ImageUploadService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        var uploadResult = cloudinary.uploader()
                .upload(
                        file.getBytes(),
                        ObjectUtils.asMap("folder", "mazadak/product_images")
                );
        return uploadResult.get("secure_url").toString();
    }
}
