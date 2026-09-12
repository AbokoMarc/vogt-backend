package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.GalleryItem;
import cm.vogt.digitalcampus.repository.GalleryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/gallery")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','COMMUNICATION_ADMIN')")
public class AdminGalleryController {

    private final GalleryItemRepository galleryItemRepository;

    @GetMapping
    public ApiResponse<List<GalleryItem>> list() {
        return ApiResponse.ok(galleryItemRepository.findAll());
    }

    @PostMapping
    public ApiResponse<GalleryItem> add(@RequestBody GalleryItem item) {
        return ApiResponse.ok("Media ajoute.", galleryItemRepository.save(item));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        galleryItemRepository.deleteById(id);
        return ApiResponse.ok("Media supprime.", null);
    }
}
