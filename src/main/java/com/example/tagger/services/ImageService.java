package com.example.tagger.services;

import com.example.tagger.models.Image;
import com.example.tagger.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private static final String IMAGE_STORAGE_PATH = "C:\\Users\\AkashJayaraj\\Documents\\Spring\\Projects\\tagger\\uploads";

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String uploadToLocalStorage(MultipartFile file) {
        Path uploadPath = Path.of(IMAGE_STORAGE_PATH);

        if(!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create upload directory", e);
            }
        }

        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(uniqueFileName);

        try {
            file.transferTo(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }

        return filePath.toString();
    }

    public String uploadImage(MultipartFile file) {
        Image image = new Image();

        String imageId = UUID.randomUUID().toString();
        String filePath = uploadToLocalStorage(file);

        image.setId(imageId);
        image.setName(file.getOriginalFilename());
        image.setSize(file.getSize());
        image.setPath(filePath);
        imageRepository.save(image);
        return imageId;
    }

}
