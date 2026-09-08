package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface LabRepository extends JpaRepository<Lab, UUID>, JpaSpecificationExecutor<Lab> {

}
