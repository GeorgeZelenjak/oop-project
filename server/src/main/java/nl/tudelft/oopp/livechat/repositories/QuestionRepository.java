package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findAllByLectureId(String lectureId);
}
