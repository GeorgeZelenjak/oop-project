package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @SuppressWarnings("unused")
    int countByUidIs(long uid);

    UserEntity getUserEntityByUid(long uid);
}
