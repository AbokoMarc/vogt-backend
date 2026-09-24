package cm.vogt.digitalcampus.repository;

import cm.vogt.digitalcampus.domain.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface StudentProjectRepository extends JpaRepository<StudentProject, UUID>, JpaSpecificationExecutor<StudentProject> {

}
