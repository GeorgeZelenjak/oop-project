package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.UserLectureSpeedRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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
     * Gets the votes for the lecture speed.
     * @param uuid the id of the lecture
     * @return the votes for the lecture speed (first number is for faster, second for slower)
     * @throws LectureException when the lecture is not found
     */
    public List<Integer> getVotes(UUID uuid) throws LectureException {
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);
        if (lecture == null) {
            throw new LectureNotFoundException();
        }
        List<Integer> numberOfVotes = new ArrayList<>();
        numberOfVotes.add(lecture.getFasterCount());
        numberOfVotes.add(lecture.getSlowerCount());
        return numberOfVotes;
    }

    /**
     * Records a user vote for the lecture speed.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param speed the indication of the lecture speed (faster or slower)
     * @return 0 if successful
     * @throws LectureException when the lecture is not found, is closed or the vote
     *          is incorrect (not "faster" or "slower")
     * @throws UserException when the user is not in the lecture
     */
    public int setUserLectureSpeedVote(long uid, UUID uuid, String speed)
            throws LectureException, UserException {
        LectureEntity lecture = validateRequest(uid, uuid, speed);
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
     * Reset the voting for the lecture speed.
     * @param uuid the id of the lecture
     * @param modKey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int resetLectureSpeed(UUID uuid, UUID modKey)
            throws LectureException, InvalidModkeyException {
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);
        if (lecture == null) {
            throw new LectureNotFoundException();
        } else if (!lecture.getModkey().equals(modKey)) {
            throw new InvalidModkeyException();
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
     * @return the lecture entity object if successful
     * @throws LectureException when the lecture is not found, is closed or the vote
     *          is incorrect (not "faster" or "slower")
     * @throws UserException when the user is not in the lecture
     */
    private LectureEntity validateRequest(long uid, UUID uuid, String speed)
            throws LectureException, UserException {
        //Check if valid speed type
        if (!speed.equals("faster") && !speed.equals("slower")) {
            throw new InvalidVoteException();
        }
        //Check if user exists
        if (userRepository.getUserEntityByUidAndLectureId(uid, uuid) == null) {
            throw new UserNotInLectureException();
        }
        //Checks if the lecture is open
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(uuid);
        if (lecture == null) {
            throw new LectureNotFoundException();
        } else if (!lectureRepository.findLectureEntityByUuid(uuid).isOpen()) {
            throw new LectureClosedException();
        }
        return lecture;
    }

    /**
     * A helper method to set the vote for the lecture.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param speed the indication of the lecture speed
     * @param lecture the lecture the user is in
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
     * @param lecture the lecture the user is in
     * @param table UserLectureSpeedTable object for recording the user's votes
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
     * A helper method to toggle the vote from "faster" to "slower" and vice versa.
     * @param uid the id of the user
     * @param uuid the id of the lecture
     * @param speed the indication of the lecture speed
     * @param lecture the lecture the user is in
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
