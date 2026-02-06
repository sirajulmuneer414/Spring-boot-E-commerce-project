package com.sirajul.lenscraft.Service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folder);
}
