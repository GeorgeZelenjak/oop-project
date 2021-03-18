package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.UserLectureSpeedPairId;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface UserLectureSpeedRepository
        extends JpaRepository<UserLectureSpeedTable, UserLectureSpeedPairId> {

    UserLectureSpeedTable findByUserIdAndLectureId(long uid, UUID uuid);

    @Transactional
    void deleteAllByLectureId(UUID uuid);

    @Transactional
    void deleteByUserIdAndLectureId(long uid, UUID uuid);

}
