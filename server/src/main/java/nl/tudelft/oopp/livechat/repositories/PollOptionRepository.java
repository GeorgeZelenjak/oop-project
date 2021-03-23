package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOptionEntity, Long> {
    PollOptionEntity findById(long id);

    List<PollOptionEntity> findAllByPollId(long id);
}
