package nl.tudelft.oopp.livechat.services;

import java.sql.Timestamp;
import java.util.*;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import org.springframework.stereotype.Service;


@Service
public class QuestionService {

    final QuestionRepository questionRepository;

    final LectureRepository lectureRepository;

    Map<Long, Set<Long>> upvoted = new HashMap<>();

    /**
     * Constructor for the question service.
     * @param questionRepository question repository
     * @param lectureRepository lecture repository
     */
    public QuestionService(QuestionRepository questionRepository,
                           LectureRepository lectureRepository) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
    }

    /**
     * Gets questions by lecture id.
     * @param lectureId the lecture id
     * @return the questions associated with the lecture id if found
     */
    public List<QuestionEntity> getQuestionsByLectureId(UUID lectureId) {
        return questionRepository.findAllByLectureId(lectureId);
    }

    /**
     * Creates new question entity in the database.
     * @param q the question entity
     * @return the id of the question entity created, -1 if question with same id already existed
     */
    public long newQuestionEntity(QuestionEntity q) {
        if (questionRepository.findById(q.getId()).isPresent()) {
            return -1;
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (!lecture.isOpen()) {
            return -1;
        }
        questionRepository.save(q);
        upvoted.put(q.getId(), new HashSet<>());
        return q.getId();
    }

    /**
     * Delete question from the database.
     * @param id the id of the question
     * @param personId the id of the person
     * @return 0 if the question is deleted successfully, -1 otherwise
     */
    public int deleteQuestion(long id, long personId) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null || q.getOwnerId() != personId) {
            return -1;
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (!lecture.isOpen()) {
            return -1;
        }
        questionRepository.deleteById(id);
        upvoted.remove(q.getId());
        return 0;
    }

    /**
     * Delete any question (done by a moderator).
     * @param id the id of the question
     * @param modkey the moderator key
     * @return 0 if the question is deleted successfully, -1 otherwise
     */
    public int deleteModeratorQuestion(long id, UUID modkey) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) {
            return -1;
        }
        LectureEntity l = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (l == null || !l.isOpen()) {
            return -1;
        }
        if (l.getModkey().equals(modkey)) {
            questionRepository.deleteById(id);
            upvoted.remove(q.getId());
            return 0;
        }
        return -1;
    }

    /**
     * Edit any question (done by a moderator).
     * @param id the id of the question
     * @param moderatorKey the moderator key
     * @param newText the new question text
     * @param newOwnerId the id of the new owner of the question
     * @return 0 if question is edited successfully, -1 otherwise
     */
    public int editQuestion(long id, UUID moderatorKey, String newText, long newOwnerId) {
        Optional<QuestionEntity> optQuestion = questionRepository.findById(id);
        if (optQuestion.isEmpty()) {
            return -1;
        }
        QuestionEntity q = optQuestion.get();
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (lecture == null || !lecture.isOpen()) {
            return -1;
        }
        if (lecture.getModkey().equals(moderatorKey)) {
            q.setText(newText);
            q.setOwnerId(newOwnerId);
            questionRepository.save(q);
            return 0;
        }
        return -1;
    }

    /**
     * Upvote question.
     * @param id the id of the question
     * @param userId the id of the user
     * @return 0 if question is upvoted successfully, -1 otherwise
     */
    public int upvote(long id, long userId) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) return -1;

        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (!lecture.isOpen()) {
            return -1;
        }

        Set<Long> voters = this.upvoted.get(q.getId());
        if (!voters.contains(userId)) {
            q.vote();
            voters.add(userId);
        } else {
            q.unvote();
            voters.remove(userId);
        }
        questionRepository.save(q);
        return 0;
    }

    /**
     * Mark a question as answered.
     *
     * @param id     the question id
     * @param modkey the modkey
     * @return 0 if successful, -1 otherwise
     */
    public int answer(long id, UUID modkey) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) return -1;

        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());

        if (lecture.getModkey().equals(modkey)) {
            q.setAnswered(true);
            q.setAnswerTime(new Timestamp(System.currentTimeMillis() % 10));
            questionRepository.save(q);
            return 0;
        }
        return -1;

    }
}
