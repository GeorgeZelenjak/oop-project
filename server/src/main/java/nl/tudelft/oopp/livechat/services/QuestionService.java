package nl.tudelft.oopp.livechat.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    LectureRepository lectureRepository;

    /**
     * Gets questions by lecture id.
     *
     * @param lectureId the lecture id
     * @return the questions by lecture id
     */
    public List<QuestionEntity> getQuestionsByLectureId(String lectureId) {
        UUID uuid = UUID.fromString(lectureId);
        return questionRepository.findAllByLectureId(uuid);
    }

    /**
     * New question entity.
     *
     * @param lectureId  the lecture id
     * @param text       the text
     * @param answerText the answer text
     * @param time       the start time
     * @param ownerId    the owner id
     * @return the question entity created
     */
    public QuestionEntity newQuestionEntity(UUID lectureId, String text,
                                            String answerText, LocalDateTime time, long ownerId) {
        QuestionEntity q = new QuestionEntity(lectureId, text, answerText, time, ownerId);
        questionRepository.save(q);
        return q;
    }

    /**
     * Delete question int.
     *
     * @param id       the id
     * @param personId the person id
     * @return the int
     */
    public int deleteQuestion(long id, long personId) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null || q.getOwnerId() != personId) {
            return -1;
        }
        questionRepository.deleteById(id);
        return 0;
    }

    /**
     * Delete moderator question int.
     *
     * @param id     the id
     * @param modkey the modkey
     * @return the int
     */
    public int deleteModeratorQuestion(long id, String modkey) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) {
            return -1;
        }
        LectureEntity l = lectureRepository.findLectureEntityByUuid(q.getLecture());
        UUID modk = UUID.fromString(modkey);
        if (l.getModkey().equals(modk)) {
            questionRepository.deleteById(id);
            return 0;
        }
        return -1;
    }

    /**
     * Edit question.
     *
     * @param id           the id
     * @param moderatorKey the moderator key
     * @param newText     the new text
     * @param newOwnerId  the new owner id
     * @return 0 if success, -1 otherwise
     */
    public int editQuestion(long id, String moderatorKey, String newText, long newOwnerId) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) {
            return -1;
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLecture());
        UUID modk = UUID.fromString(moderatorKey);
        if (lecture.getModkey().equals(modk)) {
            q.setText(newText);
            q.setOwnerId(newOwnerId);
            return 0;
        }
        return -1;
    }
}
