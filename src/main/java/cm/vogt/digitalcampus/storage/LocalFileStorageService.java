package cm.vogt.digitalcampus.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Implementation par defaut (active tant que STORAGE_PROVIDER != s3-compatible).
 * A remplacer par S3FileStorageService en production (cf. app.storage.* dans application.yml).
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.storage.local-path:./storage}")
    private String basePath;

    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        Path dir = Path.of(basePath, folder);
        Files.createDirectories(dir);

        String extension = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID() + extension;
        Path target = dir.resolve(fileName);
        Files.copy(file.getInputStream(), target);

        return "/files/" + folder + "/" + fileName;
    }
}
