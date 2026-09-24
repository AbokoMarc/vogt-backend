package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FaqItemRepository extends JpaRepository<FaqItem, UUID>, JpaSpecificationExecutor<FaqItem> {

}
