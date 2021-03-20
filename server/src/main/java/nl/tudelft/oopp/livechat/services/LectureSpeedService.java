package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.UserLectureSpeedRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
     * @param userRepository the user repository
     * @param lectureRepository the lecture repository
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
     * Gets votes.
     * @param uuid the id of the lecture
     * @return the votes
     */
    public List<Integer> getVotes(UUID uuid) {
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);

        if (lecture == null || !lectureRepository.findLectureEntityByUuid(uuid).isOpen()) {
            return null;
        }

        List<Integer> numberOfVotes = new ArrayList<>();
        numberOfVotes.add(lecture.getFasterCount());
        numberOfVotes.add(lecture.getSlowerCount());

        return numberOfVotes;
    }

    /**
     * Records a user voting.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param speed the indication of the lecture speed
     * @return  0 if everything is fine
     *         -1 if unsuccessful
     */
    public int setUserLectureSpeedVote(long uid, UUID uuid, String speed) {
        LectureEntity lecture = validateRequest(uid, uuid, speed);
        if (lecture == null) {
            return -1;
        }

        UserLectureSpeedTable userLectureSpeedTable = userLectureSpeedRepository
                .findByUserIdAndLectureId(uid, uuid);

        //Check if speed vote count does not exist yet
        if (userLectureSpeedTable == null) {
            setVote(uid, uuid, speed, lecture);

            //Check if voting twice for the same thing then delete the vote
        } else if (userLectureSpeedTable.getVoteOnLectureSpeed().equals(speed)) {
            removeVote(uid, uuid, lecture, userLectureSpeedTable);
        } else {
            toggleVote(uid, uuid, speed, lecture);
        }
        return 0;
    }

    /**
     * Reset the lecture speed.
     * @param uuid the lecture id
     * @param modKey the moderator key
     * @return  0 if everything reset successfully, -1 otherwise
     */
    public int resetLectureSpeed(UUID uuid, UUID modKey) {
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);
        if (lecture == null || !lecture.getModkey().equals(modKey)) {
            return -1;
        }
        userLectureSpeedRepository.deleteAllByLectureId(uuid);
        lecture.resetSpeedCounts();
        lectureRepository.save(lecture);
        return 0;
    }

    /**
     * A helper method too validate the request.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param speed the indication of the lecture speed
     * @return the lecture entity object iff the indication is slower/faster,
     *         the user is registered, the lecture exists and is open. Null otherwise
     */
    private LectureEntity validateRequest(long uid, UUID uuid, String speed) {
        //Check if valid speed type
        if (!speed.equals("faster") && !speed.equals("slower")) {
            return null;
        }
        //Check if user exists
        if (userRepository.getUserEntityByUidAndLectureId(uid, uuid) == null) {
            return null;
        }
        //Checks if the lecture is open
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);
        if (lecture == null || !lectureRepository.findLectureEntityByUuid(uuid).isOpen()) {
            return null;
        }
        return lecture;
    }

    /**
     * A helper method too set the vote for the lecture.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param speed the indication of the lecture speed
     * @param lecture the lecture object
     */
    private void setVote(long uid, UUID uuid, String speed, LectureEntity lecture) {
        if (speed.equals("faster")) {
            lecture.incrementFasterCount();
        } else {
            lecture.incrementSlowerCount();
        }
        lectureRepository.save(lecture);
        userLectureSpeedRepository.save(new UserLectureSpeedTable(uid, uuid, speed));
    }

    /**
     * A helper method to remove the vote for the lecture speed.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param lecture the lecture object
     * @param table UserLectureSpeedTable object
     */
    private void removeVote(long uid, UUID uuid, LectureEntity lecture,
                            UserLectureSpeedTable table) {
        userLectureSpeedRepository.deleteByUserIdAndLectureId(uid, uuid);
        if (table.getVoteOnLectureSpeed().equals("slower")) {
            lecture.decrementSlowerCount();
        } else {
            lecture.decrementFasterCount();
        }
        lectureRepository.save(lecture);
    }

    /**
     * A helper method too validate the request.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param speed the indication of the lecture speed
     * @param lecture the lecture object
     */
    private void toggleVote(long uid, UUID uuid, String speed, LectureEntity lecture) {
        if (speed.equals("faster")) {
            lecture.decrementSlowerCount();
            lecture.incrementFasterCount();
        } else {
            lecture.decrementFasterCount();
            lecture.incrementSlowerCount();
        }

        lectureRepository.save(lecture);
        userLectureSpeedRepository.save(new UserLectureSpeedTable(uid, uuid, speed));
    }


}
