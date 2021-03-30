package nl.tudelft.oopp.livechat.repositories;

import nl.tudelft.oopp.livechat.entities.UserQuestionPairId;
import nl.tudelft.oopp.livechat.entities.UserQuestionTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserQuestionRepository
        extends JpaRepository<UserQuestionTable, UserQuestionPairId> {

    @Transactional
    void deleteAllByQuestionId(long qid);

    List<UserQuestionTable> getAllByQuestionId(long qid);

    @Transactional
    void deleteAllByQuestionIdAndUserId(long qid, long uid);
}
