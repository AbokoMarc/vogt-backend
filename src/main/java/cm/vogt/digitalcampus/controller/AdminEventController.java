package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.Event;
import cm.vogt.digitalcampus.dto.content.EventRequest;
import cm.vogt.digitalcampus.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','COMMUNICATION_ADMIN')")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<java.util.List<Event>> listAll() {
        return ApiResponse.ok(eventService.listAllForAdmin());
    }

    @PostMapping
    public ApiResponse<Event> create(@Valid @RequestBody EventRequest request) {
        return ApiResponse.ok("Evenement cree.", eventService.create(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<Event> update(@PathVariable UUID id, @Valid @RequestBody EventRequest request) {
        return ApiResponse.ok("Evenement mis a jour.", eventService.update(id, request));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable UUID id) {
        eventService.publish(id);
        return ApiResponse.ok("Evenement publie.", null);
    }
}
