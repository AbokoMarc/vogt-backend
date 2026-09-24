package cm.vogt.digitalcampus.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Interface de stockage — l'implementation par defaut ecrit sur disque local
 * (utile en developpement). En production, remplacer par un adaptateur
 * S3-compatible (Cloudflare R2 / AWS S3 / Supabase Storage) sans toucher
 * au reste de l'application : seul ce contrat compte pour les services appelants.
 */
public interface FileStorageService {
    String store(MultipartFile file, String folder) throws IOException;
}
