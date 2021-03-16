package nl.tudelft.oopp.livechat.services;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.UserQuestionTable;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserQuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.stereotype.Service;


/**
 * Class for the Question service.
 */
@Service
public class QuestionService {

    final QuestionRepository questionRepository;

    final LectureRepository lectureRepository;

    final UserRepository userRepository;

    final UserQuestionRepository userQuestionRepository;

    /**
     * Constructor for the question service.
     * @param questionRepository question repository
     * @param lectureRepository lecture repository
     */
    public QuestionService(QuestionRepository questionRepository,
                           LectureRepository lectureRepository,
                           UserRepository userRepository,
                           UserQuestionRepository userQuestionRepository) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
        this.userQuestionRepository = userQuestionRepository;
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
        if (q.getText().length() > 2000) {
            return -1;
        }
        if (userRepository.findById(q.getOwnerId()).isEmpty()) {
            return -1;
        }
        UserEntity userAsked = userRepository.getUserEntityByUid(q.getOwnerId());
        q.setOwnerName(userAsked.getUserName());
        questionRepository.save(q);
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
        userQuestionRepository.deleteAllByQuestionId(id);
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
            userQuestionRepository.deleteAllByQuestionId(id);
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

        List<UserQuestionTable> votersPair = userQuestionRepository.getAllByQuestionId(id);
        List<Long> voters = votersPair.stream()
                .map(UserQuestionTable::getUserId)
                .collect(Collectors.toList());
        if (!voters.contains(userId)) {
            q.vote();
            userQuestionRepository.save(new UserQuestionTable(userId, id));
        } else {
            q.unvote();
            userQuestionRepository.deleteAllByQuestionIdAndUserId(id, userId);
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
    public int answer(long id, UUID modkey, String answerText) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) return -1;
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (answerText.length() > 2000) {
            return -1;
        }
        if (lecture.getModkey().equals(modkey)) {
            q.setAnswered(true);
            q.setAnswerTime(new Timestamp(System.currentTimeMillis() / 1000 * 1000));
            q.setAnswerText(answerText);
            questionRepository.save(q);
            return 0;
        }
        return -1;

    }
}
