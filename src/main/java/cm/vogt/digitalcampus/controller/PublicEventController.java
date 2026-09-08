package cm.vogt.digitalcampus.controller;

import cm.vogt.digitalcampus.common.ApiResponse;
import cm.vogt.digitalcampus.domain.Event;
import cm.vogt.digitalcampus.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping("/upcoming")
    public ApiResponse<List<Event>> upcoming(@RequestParam(defaultValue = "fr") String lang) {
        return ApiResponse.ok(eventService.listUpcoming(lang));
    }
}
