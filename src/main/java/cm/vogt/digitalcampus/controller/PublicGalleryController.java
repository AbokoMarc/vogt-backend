package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.repository.GalleryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/gallery")
@RequiredArgsConstructor
public class PublicGalleryController {

    private final GalleryItemRepository galleryItemRepository;

    @GetMapping
    public ApiResponse<?> list(@RequestParam(required = false) String album) {
        var all = galleryItemRepository.findAll();
        if (album != null && !album.isBlank()) {
            return ApiResponse.ok(all.stream().filter(g -> album.equalsIgnoreCase(g.getAlbum())).toList());
        }
        return ApiResponse.ok(all);
    }
}
