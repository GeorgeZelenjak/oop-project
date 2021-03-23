package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TaskScheduler taskScheduler;

    /**
     * Creates a new user service.
     *
     * @param userRepository    user repository object
     * @param lectureRepository the lecture repository
     */
    public UserService(UserRepository userRepository, LectureRepository lectureRepository) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
    }

    /**
     * Create a new user.
     *
     * @param user the user entity representing the new user
     * @param ip   the ip to be set
     * @return 0 if successful,        -1 if uid is invalid or nulls were passed
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
        if (userRepository.findById(user.getUid()).isEmpty() && count > 5) {
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
     * Toggles ban by id.
     *
     * @param modid  the moerator user id
     * @param userid the user id
     * @param modkey the modkey for the lecture the user is in
     * @param time   the time between bans and unban
     * @return 0 if ban/unban success
     *          -1 if no user with the chosen id
     *          -2 if no lecture is connected to the user
     *          -3 if the lecture is closed
     *          -4 if modkey is wrong
     *          -5 if user was already banned by someone else
     */
    public int banById(long modid, long userid, UUID modkey, int time) {
        UserEntity toBan = userRepository.getUserEntityByUid(userid);
        if (toBan == null) {
            return -1;
        }
        LectureEntity lectureIsIn = lectureRepository.findLectureEntityByUuid(toBan.getLectureId());
        if (lectureIsIn == null) {
            return -2;
        } else if (!lectureIsIn.isOpen()) {
            return -3;
        } else if (!lectureIsIn.getModkey().equals(modkey)) {
            return -4;
        } else if (toBan.getBannerId() != modid && !toBan.isAllowed()) {
            return -5;
        }
        toggleBan(toBan, modid, time);
        return 0;
    }


    /**
     * Toggles ban by ip.
     *
     * @param modid  the moderator user id
     * @param ip     the ip to ban
     * @param modkey the moderator key for 1 lecture 1 of the users could be in
     * @param time   the time of ban
     * @return   0 if success
     *          -1 if no users are present with that ip
     *          -2 if no lectures meeting the requirements
     */
    public int banByIp(long modid, String ip, UUID modkey, int time) {
        List<UserEntity> toBan = userRepository.findAllByIp(ip);
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
        toBan.forEach((u) -> toggleBan(u, modid, time));
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
        taskScheduler.schedule(
            () -> toggleBan(toUnban, modid, offset),
                new Date(OffsetDateTime.now().plusSeconds(offset).toInstant().toEpochMilli())
        );
    }

}
