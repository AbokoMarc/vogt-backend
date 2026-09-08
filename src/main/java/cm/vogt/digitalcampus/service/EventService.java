package cm.vogt.digitalcampus.service;

import cm.vogt.digitalcampus.common.NotFoundException;
import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.domain.Event;
import cm.vogt.digitalcampus.dto.content.EventRequest;
import cm.vogt.digitalcampus.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AuditLogService auditLogService;

    public List<Event> listUpcoming() {
        return listUpcoming("fr");
    }

    public List<Event> listUpcoming(String lang) {
        List<Event> events = eventRepository.findByStatusAndStartsAtAfterOrderByStartsAtAsc(PublicationStatus.PUBLISHED, Instant.now());
        events.forEach(e -> applyLang(e, lang));
        return events;
    }

    /** Ne persiste jamais : mutation en memoire du seul objet renvoye (methode non @Transactional). */
    private Event applyLang(Event e, String lang) {
        if ("en".equalsIgnoreCase(lang)) {
            if (e.getTitleEn() != null && !e.getTitleEn().isBlank()) e.setTitle(e.getTitleEn());
            if (e.getDescriptionEn() != null && !e.getDescriptionEn().isBlank()) e.setDescription(e.getDescriptionEn());
        }
        return e;
    }

    /** Utilise par le VOGT ADMIN — tous les evenements, quel que soit leur statut. */
    public List<Event> listAllForAdmin() {
        return eventRepository.findAll();
    }

    @Transactional
    public Event create(EventRequest request) {
        Event event = new Event();
        apply(event, request);
        event = eventRepository.save(event);
        auditLogService.log("CREATE_EVENT", "Event", event.getId().toString(), "Evenement cree : " + event.getTitle());
        return event;
    }

    @Transactional
    public Event update(UUID id, EventRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Evenement introuvable."));
        apply(event, request);
        event = eventRepository.save(event);
        auditLogService.log("UPDATE_EVENT", "Event", event.getId().toString(), "Evenement modifie : " + event.getTitle());
        return event;
    }

    @Transactional
    public void publish(UUID id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Evenement introuvable."));
        event.setStatus(PublicationStatus.PUBLISHED);
        eventRepository.save(event);
        auditLogService.log("PUBLISH_EVENT", "Event", event.getId().toString(), "Evenement publie : " + event.getTitle());
    }

    private void apply(Event event, EventRequest request) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setTitleEn(request.getTitleEn());
        event.setDescriptionEn(request.getDescriptionEn());
        event.setLocation(request.getLocation());
        event.setStartsAt(request.getStartsAt());
        event.setEndsAt(request.getEndsAt());
        event.setCoverImageUrl(request.getCoverImageUrl());
        event.setRegistrationRequired(request.isRegistrationRequired());
        event.setRegistrationUrl(request.getRegistrationUrl());
    }
}
