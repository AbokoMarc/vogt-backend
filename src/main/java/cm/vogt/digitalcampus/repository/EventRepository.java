package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.common.enums.PublicationStatus;
import cm.vogt.digitalcampus.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByStatusAndStartsAtAfterOrderByStartsAtAsc(PublicationStatus status, Instant after);
}
