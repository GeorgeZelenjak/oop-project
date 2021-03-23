package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.poll.UserPollVotePairId;
import nl.tudelft.oopp.livechat.entities.poll.UserPollVoteTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserPollVoteRepository extends JpaRepository<UserPollVoteTable,
        UserPollVotePairId> {
    List<UserPollVoteTable> findAllByUserId(long uid);

    @Transactional
    void deleteAllByOptionId(long optionId);
}
