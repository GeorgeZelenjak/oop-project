package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserQuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class UserService {

    private final UserRepository userRepository;

    private final LectureRepository lectureRepository;

    private final QuestionRepository questionRepository;

    private final UserQuestionRepository userQuestionRepository;

    private final TaskScheduler taskScheduler;

    /**
     * Creates new UserService object.
     * @param userRepository the user repository
     * @param lectureRepository the lecture repository
     * @param questionRepository the question repository
     * @param userQuestionRepository the user question repository
     * @param taskScheduler the task scheduler for banning
     */
    public UserService(UserRepository userRepository, LectureRepository lectureRepository,
                       QuestionRepository questionRepository,
                       UserQuestionRepository userQuestionRepository, TaskScheduler taskScheduler) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.questionRepository = questionRepository;
        this.userQuestionRepository = userQuestionRepository;
        this.taskScheduler = taskScheduler;
    }

    /**
     * Registers a user to the repository.
     * @param user the new user
     * @param ip the ip address of the user
     * @return 0 iff the user is registered successfully
     * @throws UserException when the user id is not a valid mac address
     *              or there are more that 5 users with the same IP-address
     * @throws NoDataReceivedException when the user object is null
     */
    public int newUser(UserEntity user, String ip) throws UserException, NoDataReceivedException {
        if (user == null) {
            throw new NoDataReceivedException();
        }

        user.setIp(ip);
        if (!luhnCheck(user.getUid())) { // use mac address checksum
            throw new UserIdNotValidException();
        }
        int count = userRepository.countAllByIp(ip);
        if (userRepository.findById(user.getUid()).isEmpty() && count > 5) {
            throw new UserTooManyUsersException();
        }
        userRepository.save(user);
        return 0;
    }

    /**
     * Luhn check for the given number.
     * @param n the number
     * @return true if checksum is valid, false otherwise
     */
    public static boolean luhnCheck(long n) {
        String number = Long.toString(n);
        long temp = 0;
        for (int i = number.length() - 1;i >= 0;i--) {
            int digit;
            if ((number.length() - i) % 2 == 0) {
                digit = Character.getNumericValue(number.charAt(i)) * 2;
                if (digit > 9) {
                    digit %= 9;
                    if (digit == 0) digit = 9;
                }
            } else {
                digit = Character.getNumericValue(number.charAt(i));
            }
            temp += digit;
        }
        return temp % 10 == 0;
    }

    /**
     * Bans the user by id.
     * @param modid the moderator's user id
     * @param qid the question id which user is to be banned
     * @param modkey the modkey for the lecture the user is in
     * @param time the time for the ban
     * @return 0 if banned successfully
     * @throws UserException when the user is not registered or already banned
     * @throws LectureException when the lecture is not found or is closed
     * @throws QuestionException when the question is not found
     * @throws InvalidModkeyException when the modkey is incorrect
     */
    public int banById(long modid, long qid, UUID modkey, int time)
                        throws UserException, LectureException,
                                QuestionException, InvalidModkeyException {
        QuestionEntity incriminated = questionRepository.findById(qid).orElse(null);
        if (incriminated == null) {
            throw new QuestionNotFoundException();
        }
        UserEntity toBan = userRepository.getUserEntityByUid(incriminated.getOwnerId());
        if (toBan == null) {
            throw new UserNotRegisteredException();
        }
        LectureEntity lectureIsIn = lectureRepository.findLectureEntityByUuid(toBan.getLectureId());
        if (lectureIsIn == null) {
            throw new LectureNotFoundException();
        } else if (!lectureIsIn.isOpen()) {
            throw new LectureClosedException();
        } else if (!lectureIsIn.getModkey().equals(modkey)) {
            throw new InvalidModkeyException();
        } else if (!toBan.isAllowed()) {
            throw new UserBannedException();
        }
        toggleBan(toBan, modid, time);
        editRepositoryAfterBanning(qid, toBan.getUid());
        return 0;
    }


    /**
     * Bans the user by ip.
     * @param modid the moderator's user id
     * @param qid the question id which user is to be banned
     * @param modkey the moderator key for one of the lectures the user could be in
     * @param time the time for the ban
     * @return 0 if banned successfully
     * @throws UserException when the user(s) is not registered or already banned
     * @throws LectureException when the lecture is not found
     * @throws QuestionException when the question is not found
     */
    public int banByIp(long modid, long qid, UUID modkey, int time)
            throws UserException, LectureException, QuestionException {
        QuestionEntity incriminated = questionRepository.findById(qid).orElse(null);
        if (incriminated == null) {
            throw new QuestionNotFoundException();
        }
        UserEntity user = userRepository.getUserEntityByUid(incriminated.getOwnerId());
        if (user == null) {
            throw new UserNotRegisteredException();
        }
        List<UserEntity> toBan = userRepository.findAllByIp(user.getIp());
        List<LectureEntity> lectureIn = toBan.stream()
                .map((u) -> lectureRepository.findLectureEntityByUuid(u.getLectureId()))
                .filter(LectureEntity::isOpen)
                .filter((l) -> l.getModkey().equals(modkey))
                .collect(Collectors.toList());
        if (lectureIn.size() == 0) {
            throw new LectureNotFoundException();
        }
        toBan = toBan.stream().filter(UserEntity::isAllowed).collect(Collectors.toList());
        if (toBan.isEmpty()) {
            throw new UserBannedException();
        }

        toBan.forEach((u) -> {
            toggleBan(u, modid, time);
            editRepositoryAfterBanning(qid, u.getUid());
        });
        return 0;
    }


    /**
     * A helper method to schedule ban, or unban the user.
     * @param toBan the user to be banned or unbanned
     * @param modid the moderator id (not key)
     * @param time the time of the ban in seconds (the value is not considered when unbanning)
     */
    private void toggleBan(UserEntity toBan, long modid, int time) {
        if (toBan.isAllowed()) {
            toBan.setBannerId(modid);
            scheduleUnban(toBan, time, modid);
        } else {
            toBan.setBannerId(0);
        }
        toBan.setAllowed(!toBan.isAllowed());
        userRepository.save(toBan);
    }


    /**
     * A helper method to schedule unban.
     * @param toUnban the user to be banned or unbanned
     * @param offset the time of the ban in seconds (the value is not considered when unbanning)
     * @param modid the moderator id (not key)
     */
    private void scheduleUnban(UserEntity toUnban, int offset, long modid) {
        taskScheduler.schedule(() -> {
            UserEntity user = userRepository.findById(toUnban.getUid()).orElse(null);
            if (user == null) {
                return;
            }
            toggleBan(toUnban, modid, offset);
            editRepositoryAfterUnbanning(toUnban.getUid());
        }, new Date(OffsetDateTime.now().plusSeconds(offset).toInstant().toEpochMilli()));
    }

    /**
     * A helper method to save the changes and modify user- and question repositories
     *      after the ban has happened.
     * @param qid the id of the question asked by the banned user
     * @param uid the id of the banned user
     */
    private void editRepositoryAfterBanning(long qid, long uid) {
        if (questionRepository.findById(qid).isPresent()) {
            questionRepository.deleteById(qid);
        }
        if (!userQuestionRepository.getAllByQuestionId(qid).isEmpty()) {
            userQuestionRepository.deleteAllByQuestionId(qid);
        }

        List<QuestionEntity> qs = questionRepository.findAllByOwnerId(uid);
        qs.forEach(q -> q.setOwnerName(q.getOwnerName() + " (banned)"));
        questionRepository.saveAll(qs);
    }

    /**
     * A helper method to save the changes and modify user- and question repositories
     *      after the ban has ended.
     * @param uid the id of the banned user
     */
    private void editRepositoryAfterUnbanning(long uid) {
        List<QuestionEntity> qs = questionRepository.findAllByOwnerId(uid);
        qs.forEach(q -> q.setOwnerName(q.getOwnerName()
                .substring(0, q.getOwnerName().length() - 9)));
        questionRepository.saveAll(qs);
    }

}
