package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.UserLectureSpeedPairId;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import nl.tudelft.oopp.livechat.services.LectureService;
import nl.tudelft.oopp.livechat.services.LectureSpeedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserLectureSpeedRepository extends JpaRepository<UserLectureSpeedTable,
        UserLectureSpeedPairId> {
    UserLectureSpeedTable findAllByUidAndUuid(long uid, UUID uuid);

    void deleteAllByUuid(UUID uuid);

    void deleteByUidAndUuid(long uid, UUID uuid);

}
