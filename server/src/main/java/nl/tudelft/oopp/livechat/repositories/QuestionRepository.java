package nl.tudelft.oopp.livechat.repositories;

import java.util.List;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findAllByLectureId(UUID lectureId);
}
