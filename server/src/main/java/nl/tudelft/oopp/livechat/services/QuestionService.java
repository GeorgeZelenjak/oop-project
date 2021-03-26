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
    public QuestionService(QuestionRepository questionRepository,
                           LectureRepository lectureRepository, UserRepository userRepository,
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
     * @return the id of the question entity created
     * @throws LectureException when the lecture is not found, is closed or is not started
     * @throws QuestionException when the question with the same id already exists, the question
     *          text is too long, or the user asks questions too frequently
     * @throws UserException when the user is not registered or banned
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

        Optional<UserEntity> user = userRepository.findById(q.getOwnerId());

        //check if the owner is registered
        if (user.isEmpty()) {
            throw new UserNotRegisteredException();
        }


        UserEntity userAsked = userRepository.getUserEntityByUid(q.getOwnerId());
        if (!userAsked.isAllowed()) {
            throw new UserBannedException();
        }
        if (userAsked.getLastQuestion() != null
                && System.currentTimeMillis() - userAsked.getLastQuestion().getTime()
                < lecture.getFrequency() * 1000) {
            throw new QuestionFrequencyTooFastException();
        }
        userAsked.setLastQuestion(new Timestamp(System.currentTimeMillis() / 1000 * 1000));
        q.setOwnerName(userAsked.getUserName());
        questionRepository.save(q);
        userRepository.save(userAsked);
        return q.getId();
    }

    /**
     * Delete question from the database.
     * @param id the id of the question
     * @param personId the id of the person
     * @return 0 if the question is deleted successfully
     * @throws QuestionException when the question is not found
     *          or the owner id doesn't match the provided id
     * @throws LectureException when the lecture is not found or is closed
     * @throws UserException when the user is not registered
     */
    public int deleteQuestion(long id, long personId)
            throws QuestionException, LectureException, UserException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question and lecture exist
        LectureEntity lecture = validateQuestionAndFindLecture(q);
        //check if the lecture is open
        if (!lecture.isOpen()) {
            throw new LectureClosedException();
        }
        //check if the ids match
        if (q.getOwnerId() != personId) {
            throw new QuestionWrongOwnerIdException();
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
     * @return 0 if the question is deleted successfully
     * @throws QuestionException when the question is not found
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int deleteModeratorQuestion(long id, UUID modkey)
            throws QuestionException, LectureException, InvalidModkeyException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question and lecture exist
        LectureEntity lecture = validateQuestionAndFindLecture(q);
        //check if the modkey is correct
        if (lecture.getModkey().equals(modkey)) {
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
     * @return 0 if question is edited successfully
     * @throws QuestionException when the question is not found or the new question text is too long
     * @throws LectureException when the lecture is not found
     * @throws UserException when the new owner is not registered
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int editQuestion(long id, UUID moderatorKey, String newText, long newOwnerId)
            throws QuestionException, LectureException, UserException, InvalidModkeyException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question and lecture exist
        LectureEntity lecture = validateQuestionAndFindLecture(q);
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
     * @return 0 if question is upvoted successfully
     * @throws QuestionException when the question is not found
     * @throws LectureException when the lecture is not found or is closed
     * @throws UserException when the user is not registered
     */
    public int upvote(long id, long userId)
            throws QuestionException, LectureException, UserException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question and lecture exist
        LectureEntity lecture = validateQuestionAndFindLecture(q);
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
     * @return 0 if successful
     * @throws QuestionException when the question is not found or the answer text is too long
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int answer(long id, UUID modkey, String answerText)
            throws QuestionException, LectureException, InvalidModkeyException {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        //check if the question and lecture exist
        LectureEntity lecture = validateQuestionAndFindLecture(q);
        //check if the answer text is not too long
        if (answerText.length() > 2000) {
            throw new QuestionNotModifiedException();
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

    /**
     * Changes the status of the question when a moderator is editing or answering it.
     * @param status the status of the question
     * @param qid the id of the question
     * @param uid the id of the user
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureNotFoundException when the lecture is not found
     * @throws QuestionException when the question is not found or is already modified
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int setStatus(String status, long qid, long uid, UUID modkey)
            throws LectureNotFoundException, QuestionException, InvalidModkeyException {
        QuestionEntity q = questionRepository.findById(qid).orElse(null);
        LectureEntity lecture = validateQuestionAndFindLecture(q);

        if (q.getEditorId() != 0 && q.getEditorId() != uid) {
            throw new QuestionAlreadyBeingModifiedException();
        }
        if (lecture.getModkey().equals(modkey)) {
            q.setStatus(status);
            if (status.equals("new")) {
                q.setEditorId(0);
            } else {
                q.setEditorId(uid);
            }
            questionRepository.save(q);
            return 0;
        }
        throw new InvalidModkeyException();
    }

    /**
     * A helper method to check if the question and the lecture exist in the database.
     * @param q the question to check
     * @return the lecture in which the question was asked if it exists
     * @throws QuestionException when the question is not found
     * @throws LectureNotFoundException when the lecture is not found
     */
    private LectureEntity validateQuestionAndFindLecture(QuestionEntity q)
            throws QuestionException, LectureNotFoundException {
        //check if the question exists
        if (q == null) {
            throw new QuestionNotFoundException();
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLectureId());
        //check if the lecture exists
        if (lecture == null) {
            throw new LectureNotFoundException();
        }
        return lecture;
    }
}
