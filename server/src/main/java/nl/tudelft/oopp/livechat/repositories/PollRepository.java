package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.PollEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<PollEntity, Long> {
    PollEntity findById(long id);

    PollEntity findFirstByUuidOrderByTimeDesc(UUID uuid);
}
