package com.fnmusic.user.management.services;

import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.base.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    private Path fileLocation;
    private static Logger logger = LoggerFactory.getLogger(StorageService.class);

    @SuppressWarnings("ignored")
    @PostConstruct
    public void resolveDirectories() {
        try {
            File dir = new File(ConstantUtils.PHOTO_STORAGE_PATH);
            if (dir.mkdirs()) {
                logger.info("Created Photo Storage Directory");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public String storeFile(MultipartFile file, String name) {
        try {
            String filename = ConstantUtils.PHOTO_STORAGE_PATH + SystemUtils.getCurrentUser().getEmail().concat("_" + name) + getFileExtension(file.getOriginalFilename());
            Path targetLocation = Paths.get(filename);
            Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
