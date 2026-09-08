package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AlumniRepository extends JpaRepository<Alumni, UUID>, JpaSpecificationExecutor<Alumni> {

}
