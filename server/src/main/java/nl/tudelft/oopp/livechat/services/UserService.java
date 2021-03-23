package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
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

    final UserRepository userRepository;

    final LectureRepository lectureRepository;

    final QuestionRepository questionRepository;

    final UserQuestionRepository userQuestionRepository;

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
     * Creates a new user.
     * @param user the user entity representing the new user
     * @param ip the ip to be set
     * @return 0 if successful, -1 if uid is invalid or nulls were passed
     */
    public int newUser(UserEntity user, String ip) {
        if (user == null || ip == null) {
            return -1;
        }
        user.setIp(ip);
        if (!luhnCheck(user.getUid())) { // use mac address checksum
            return -1;
        }
        int count = userRepository.countAllByIp(ip);
        if (userRepository.findById(user.getUid()).isEmpty() && count >= 5) {
            return -1;
        }
        userRepository.save(user);
        return 0;
    }

    /**
     * Luhn check for the given number.
     *
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
     * @param modid  the moderator's user id
     * @param qid the question id which user is to be banned
     * @param modkey the modkey for the lecture the user is in
     * @param time the time for the ban
     * @return 0 if banned successfully
     *          -1 if no user with the chosen id
     *          -2 if no lecture is connected to the user
     *          -3 if the lecture is closed
     *          -4 if modkey is wrong
     *          -5 if user was already banned
     */
    public int banById(long modid, long qid, UUID modkey, int time) {
        QuestionEntity incriminated = questionRepository.findById(qid).orElse(null);
        if (incriminated == null) {
            return -1;
        }
        UserEntity toBan = userRepository.getUserEntityByUid(incriminated.getOwnerId());
        if (toBan == null) {
            System.out.println("The user with this id does not exist");
            return -1;
        }
        LectureEntity lectureIsIn = lectureRepository.findLectureEntityByUuid(toBan.getLectureId());
        if (lectureIsIn == null) {
            return -2;
        } else if (!lectureIsIn.isOpen()) {
            return -3;
        } else if (!lectureIsIn.getModkey().equals(modkey)) {
            return -4;
        } else if (!toBan.isAllowed()) {
            return -5;
        }
        toggleBan(toBan, modid, time);
        editRepositoryAfterBanning(qid, toBan.getUid());
        return 0;
    }


    /**
     * Bans the user by ip.
     * @param modid the moderator's user id
     * @param qid the question id which user is to be banned
     * @param modkey the moderator key for 1 of the lectures the user could be in
     * @param time the time for the ban
     * @return 0 if banned successfully
     *          -1 if no user with the chosen ip
     *          -2 if no lectures meet the requirements
     *          -3 if the user is already banned
     */
    public int banByIp(long modid, long qid, UUID modkey, int time) {
        QuestionEntity incriminated = questionRepository.findById(qid).orElse(null);
        if (incriminated == null) {
            return -1;
        }
        UserEntity user = userRepository.getUserEntityByUid(incriminated.getOwnerId());
        if (user == null) {
            return -1;
        }
        List<UserEntity> toBan = userRepository.findAllByIp(user.getIp());
        if (toBan == null || toBan.size() == 0) {
            return -1;
        }
        List<LectureEntity> lectureIn = toBan.stream()
                .map((u) -> lectureRepository.findLectureEntityByUuid(u.getLectureId()))
                .filter(LectureEntity::isOpen)
                .filter((l) -> l.getModkey().equals(modkey))
                .collect(Collectors.toList());
        if (lectureIn.size() == 0) {
            return -2;
        }
        toBan = toBan.stream().filter(UserEntity::isAllowed).collect(Collectors.toList());
        if (toBan.isEmpty()) {
            return -3;
        }

        toBan.forEach((u) -> {
            toggleBan(u, modid, time);
            editRepositoryAfterBanning(qid, u.getUid());
        });
        return 0;
    }


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

    private void editRepositoryAfterUnbanning(long uid) {
        List<QuestionEntity> qs = questionRepository.findAllByOwnerId(uid);
        qs.forEach(q -> q.setOwnerName(q.getOwnerName()
                .substring(0, q.getOwnerName().length() - 9)));
        questionRepository.saveAll(qs);
    }

}
