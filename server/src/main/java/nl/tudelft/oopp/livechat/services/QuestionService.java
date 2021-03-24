package nl.tudelft.oopp.livechat.services;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.UserQuestionTable;
import nl.tudelft.oopp.livechat.exceptions.*;
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

    private final QuestionRepository questionRepository;

    private final LectureRepository lectureRepository;

    private final UserRepository userRepository;

    private final UserQuestionRepository userQuestionRepository;

    /**
     * Constructor for the question service.
     * @param questionRepository question repository
     * @param lectureRepository lecture repository
     * @param userRepository user repository
     * @param userQuestionRepository user-question repository
     */
    public QuestionService(QuestionRepository questionRepository, LectureRepository lectureRepository,
                           UserRepository userRepository, UserQuestionRepository userQuestionRepository) {
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
     * @return the id of the question entity created, -1 if not
     */
    public long newQuestionEntity(QuestionEntity q)
            throws LectureException, QuestionException, UserException {
        //check if the question already exists
        if (questionRepository.findById(q.getId()).isPresent()) {
            throw new QuestionAlreadyExistsException();
        }
        //check if the lecture exists and is open
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (lecture == null) {
            throw new LectureNotFoundException();
        } else if (!lecture.isOpen()) {
            throw new LectureClosedException();
        }

        //check if lecture has started
        if (lecture.getStartTime().compareTo(new Timestamp(System.currentTimeMillis())) >= 0) {
            throw new LectureNotStartedException();
        }

        //check if the question text is not too long
        if (q.getText().length() > 2000) {
            throw new QuestionNotAskedException();
        }
        //check if the owner is registered
        if (userRepository.findById(q.getOwnerId()).isEmpty()) {
            throw new UserNotRegisteredException();
        }
        UserEntity userAsked = userRepository.getUserEntityByUid(q.getOwnerId());
        if (!userAsked.isAllowed()) {
            throw new UserBannedException();
        }
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
    public int deleteQuestion(long id, long personId)
            throws QuestionException, LectureException, UserException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question exists and the owner ids are equal
        if (q == null) {
            throw new QuestionNotFoundException();
        } else if (q.getOwnerId() != personId) {
            throw new QuestionWrongOwnerIdException();
        }

        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        //check if the lecture exists and is open
        if (lecture == null) {
            throw new LectureNotFoundException();
        } else if (!lecture.isOpen()) {
            throw new LectureClosedException();
        }

        //check if the owner is registered
        if (userRepository.findById(personId).isEmpty()) {
            throw new UserNotRegisteredException();
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
    public int deleteModeratorQuestion(long id, UUID modkey)
            throws QuestionException, LectureException, InvalidModkeyException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question exists
        if (q == null) {
            throw new QuestionNotFoundException();
        }
        LectureEntity l = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        //check if the lecture exists
        if (l == null) {
            throw new LectureNotFoundException();
        }
        //check if the modkey is correct
        if (l.getModkey().equals(modkey)) {
            questionRepository.deleteById(id);
            userQuestionRepository.deleteAllByQuestionId(id);
            return 0;
        }
        throw new InvalidModkeyException();
    }

    /**
     * Edit any question (done by a moderator).
     * @param id the id of the question
     * @param moderatorKey the moderator key
     * @param newText the new question text
     * @param newOwnerId the id of the new owner of the question
     * @return 0 if question is edited successfully, -1 otherwise
     */
    public int editQuestion(long id, UUID moderatorKey, String newText, long newOwnerId)
            throws QuestionException, LectureException, UserException, InvalidModkeyException {
        Optional<QuestionEntity> optQuestion = questionRepository.findById(id);
        //check if the question exists
        if (optQuestion.isEmpty()) {
            throw new QuestionNotFoundException();
        }
        QuestionEntity q = optQuestion.get();
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        //check if the lecture exists
        if (lecture == null) {
            throw new LectureNotFoundException();
        }
        //check if the modkey is correct
        if (lecture.getModkey().equals(moderatorKey)) {
            //check if the question text is not too long
            if (newText.length() > 2000) {
                throw new QuestionNotModifiedException();
            }
            //check if the new owner is registered
            UserEntity user = userRepository.findById(newOwnerId).orElse(null);
            if (user == null) {
                throw new UserNotRegisteredException();
            }
            q.setText(newText);
            q.setOwnerId(newOwnerId);
            q.setOwnerName(user.getUserName());
            q.setEdited(true);
            questionRepository.save(q);
            return 0;
        }
        throw new InvalidModkeyException();
    }

    /**
     * Upvote question.
     * @param id the id of the question
     * @param userId the id of the user
     * @return 0 if question is upvoted successfully, -1 otherwise
     */
    public int upvote(long id, long userId)
            throws QuestionException, LectureException, UserException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question exists
        if (q == null) {
            throw new QuestionNotFoundException();
        }

        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        //check if the lecture exists
        if (lecture == null) {
            throw new LectureNotFoundException();
        }
        //check if the lecture is open
        if (!lecture.isOpen()) {
            throw new LectureClosedException();
        }

        //check if the user is registered
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotRegisteredException();
        }

        List<UserQuestionTable> votersPair = userQuestionRepository.getAllByQuestionId(id);
        List<Long> voters = votersPair.stream()
                .map(UserQuestionTable::getUserId)
                .collect(Collectors.toList());
        //check if the user has already upvoted the question
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
     * Mark question as answered.
     * @param id the question id
     * @param modkey the modkey
     * @param answerText the answer text
     * @return 0 if successful, -1 otherwise
     */
    public int answer(long id, UUID modkey, String answerText)
            throws QuestionException, LectureException, InvalidModkeyException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question exists
        if (q == null) {
            throw new QuestionNotFoundException();
        }
        //check if the answer text is not too long
        if (answerText.length() > 2000) {
            throw new QuestionNotModifiedException();
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        //check if the lecture exists
        if (lecture == null) {
            throw new LectureNotFoundException();
        }
        //check if the modkey is correct
        if (lecture.getModkey().equals(modkey)) {
            q.setAnswered(true);
            q.setAnswerTime(new Timestamp(System.currentTimeMillis() / 1000 * 1000));
            q.setAnswerText(answerText);
            questionRepository.save(q);
            return 0;
        }
        throw new InvalidModkeyException();
    }

    private LectureEntity validateQuestionAndFindLecture(QuestionEntity q)
            throws QuestionException, LectureNotFoundException {
        if (q == null) {
            throw new QuestionNotFoundException();
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        if (lecture == null) {
            throw new LectureNotFoundException();
        }
        return lecture;
    }
}
