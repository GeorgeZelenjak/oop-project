package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<LectureEntity, String> {
    LectureEntity findLectureEntityByUuid(String id);
}
