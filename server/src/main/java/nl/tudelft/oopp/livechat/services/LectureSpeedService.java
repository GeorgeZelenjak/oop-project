package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.UserLectureSpeedRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * The type Lecture speed service.
 */
@Service
public class LectureSpeedService {

    /**
     * The User repository.
     */
    final UserRepository userRepository;
    /**
     * The Lecture repository.
     */
    final LectureRepository lectureRepository;
    /**
     * The User lecture speed repository.
     */
    final UserLectureSpeedRepository userLectureSpeedRepository;

    /**
     * Instantiates a new Lecture speed service.
     *
     * @param userRepository             the user repository
     * @param lectureRepository          the lecture repository
     * @param userLectureSpeedRepository the user lecture speed repository
     */
    public LectureSpeedService(UserRepository userRepository,
                               LectureRepository lectureRepository,
                               UserLectureSpeedRepository userLectureSpeedRepository) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.userLectureSpeedRepository = userLectureSpeedRepository;
    }

    /**
     * Records a user voting.
     *
     * @param uid   the uid
     * @param uuid  the uuid
     * @param speed the speed
     * @return  0 if everything is fine
     *         -1 if unsuccessful
     */
    public int setUserLectureSpeedVote(long uid, UUID uuid,String speed) {
        //Check if valid speed type
        if (!speed.equals("faster") && !speed.equals("slower")) {
            return -1;
        }
        //Check if user exists
        if (userRepository.getUserEntityByUidAndLectureId(uid,uuid) == null) {
            return -1;
        }
        //Checks if the lecture is open
        if (!lectureRepository.findLectureEntityByUuid(uuid).isOpen()) {
            return -1;
        }

        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);
        UserLectureSpeedTable userLectureSpeedTable = userLectureSpeedRepository
                .findAllByUidAndUuid(uid,uuid);

        //Check if speed vote count does not exist yet
        if (userLectureSpeedTable == null) {
            if (speed.equals("faster")) {
                lecture.incrementFasterCount();
            } else {
                lecture.incrementSlowerCount();
            }
            lectureRepository.save(lecture);
            userLectureSpeedRepository.save(new UserLectureSpeedTable(uid, uuid, speed));
            return 0;
        }

        //Check if voting twice for the same thing then delete the vote
        if (userLectureSpeedTable.getVoteOnLectureSpeed().equals(speed)) {
            userLectureSpeedRepository.deleteByUidAndUuid(uid,uuid);
            if (userLectureSpeedTable.getVoteOnLectureSpeed().equals("slower")) {
                lecture.decrementSlowerCount();
            } else {
                lecture.decrementFasterCount();
            }
            lectureRepository.save(lecture);
            return 0;
        }

        //If the vote is different from the previous one
        if (speed.equals("faster")) {
            lecture.decrementSlowerCount();
            lecture.incrementFasterCount();
        } else {
            lecture.decrementFasterCount();
            lecture.incrementSlowerCount();
        }

        lectureRepository.save(lecture);
        userLectureSpeedRepository.save(new UserLectureSpeedTable(uid, uuid, speed));
        return 0;
    }

    /**
     * Reset lecture speed.
     *
     * @param uuid   the uuid
     * @param modKey the mod key
     * @return  0 if everything is fine
     *         -1 if unsuccessful
     */
    public int resetLectureSpeed(UUID uuid, UUID modKey) {
        LectureService lectureService = new LectureService(lectureRepository);
        if (lectureService.validateModerator(uuid,modKey) != 0) {
            return -1;
        }
        userLectureSpeedRepository.deleteAllByUuid(uuid);
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);
        lecture.resetSpeedCounts();
        lectureRepository.save(lecture);
        return 0;
    }
}
