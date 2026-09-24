package cm.vogt.digitalcampus.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * Adaptateur reel pour tout stockage compatible S3 : Cloudflare R2, AWS S3,
 * Supabase Storage, MinIO... Active uniquement quand STORAGE_PROVIDER=s3-compatible
 * ET que les identifiants sont fournis (voir application.yml / .env.example).
 *
 * Pour l'activer en production, renseignez dans les variables d'environnement :
 *   STORAGE_ENDPOINT, STORAGE_ACCESS_KEY, STORAGE_SECRET_KEY, STORAGE_BUCKET
 * (STORAGE_REGION optionnel, "auto" convient pour Cloudflare R2).
 *
 * Tant que ces variables ne sont pas renseignees, l'application utilise
 * automatiquement LocalFileStorageService — aucun code a changer.
 */
@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3-compatible")
public class S3FileStorageService implements FileStorageService {

    @Value("${app.storage.endpoint:}")
    private String endpoint;

    @Value("${app.storage.access-key:}")
    private String accessKey;

    @Value("${app.storage.secret-key:}")
    private String secretKey;

    @Value("${app.storage.bucket}")
    private String bucket;

    @Value("${app.storage.region:auto}")
    private String region;

    @Value("${app.storage.public-base-url:}")
    private String publicBaseUrl;

    private S3Client client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region.equals("auto") ? "us-east-1" : region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        if (endpoint == null || endpoint.isBlank() || accessKey == null || accessKey.isBlank()) {
            throw new IllegalStateException(
                    "Stockage S3-compatible active mais identifiants manquants (STORAGE_ENDPOINT / STORAGE_ACCESS_KEY / STORAGE_SECRET_KEY).");
        }

        String extension = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf('.'));
        }
        String key = folder + "/" + UUID.randomUUID() + extension;

        try (S3Client s3 = client()) {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        }

        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            return publicBaseUrl.replaceAll("/$", "") + "/" + key;
        }
        return endpoint.replaceAll("/$", "") + "/" + bucket + "/" + key;
    }
}
