package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @SuppressWarnings("unused")
    int countByUidIs(long uid);

    UserEntity getUserEntityByUid(long uid);

    UserEntity getUserEntityByUidAndLectureId(long uid, UUID uuid);

    int countAllByIp(String ip);

    List<UserEntity> findAllByIp(String ip);
}
