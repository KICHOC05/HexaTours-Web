package com.codeboost.travel_agency_backend_template.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(@Value("${cloudinary.url}") String cloudinaryUrl) {
        if (cloudinaryUrl == null
                || cloudinaryUrl.isBlank()
                || !cloudinaryUrl.startsWith("cloudinary://")) {
            throw new IllegalStateException(
                    "CLOUDINARY_URL must use the cloudinary://<api-key>:<api-secret>@<cloud-name> format");
        }

        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        cloudinary.config.secure = true;
        return cloudinary;
    }
}
