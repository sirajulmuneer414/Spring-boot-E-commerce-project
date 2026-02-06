package com.sirajul.lenscraft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path productUploadDir = Paths.get("./productImages");
        String productUploadPath = productUploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/productImages/**").addResourceLocations("file:/" + productUploadPath + "/");

        Path profileUploadDir = Paths.get("./profileImages");
        String profileUploadPath = profileUploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/profilePic/**").addResourceLocations("file:/" + profileUploadPath + "/");
    }
}
