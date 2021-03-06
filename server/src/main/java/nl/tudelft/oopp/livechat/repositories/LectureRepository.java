package nl.tudelft.oopp.livechat.repositories;

import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, UUID> {
    LectureEntity findLectureEntityByUuid(UUID id);
}
