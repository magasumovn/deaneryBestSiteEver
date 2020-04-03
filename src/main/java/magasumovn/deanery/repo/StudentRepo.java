package magasumovn.deanery.repo;

import magasumovn.deanery.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepo extends JpaRepository<Student, Long> {
    List<Student> findByGroupGroupName(String groupName);
}
