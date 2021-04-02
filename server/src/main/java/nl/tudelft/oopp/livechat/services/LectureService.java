package nl.tudelft.oopp.livechat.services;

import java.sql.Timestamp;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.springframework.stereotype.Service;


/**
 * Class for the Lecture service.
 */
@Service
public class LectureService {

    /**
     * The Lecture repository.
     */
    final LectureRepository lectureRepository;

    /**
     * Constructor for the lecture service.
     *
     * @param lectureRepository lecture repository
     */
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    /**
     * Gets lecture by id without the moderator key.
     * @param id the id of the lecture
     * @return the lecture if the id is found in the database
     * @throws LectureException when the lecture is not found or is not started yet
     */
    public LectureEntity getLectureByIdNoModkey(UUID id) throws LectureException {
        LectureEntity toSend = lectureRepository.findLectureEntityByUuid(id);
        if (toSend == null) {
            throw new LectureNotFoundException();
        }

        //check if lecture has started
        if (toSend.getStartTime().compareTo(new Timestamp(System.currentTimeMillis())) >= 0) {
            throw new LectureNotStartedException();
        }
        toSend.setModkey(null);
        return toSend;
    }

    /**
     * Gets lecture by id and exposes the moderator key.
     * @param id the id of the lecture
     * @return the lecture entity
     */
    private LectureEntity getLectureById(UUID id) {
        return lectureRepository.findLectureEntityByUuid(id);
    }

    /**
     * Creates a new lecture in the database.
     * @param name the name of the lecture
     * @param creatorName the name of the creator
     * @param startTime the start time of the lecture
     * @param frequency the frequency of asking questions
     * @return the new lecture entity
     * @throws LectureNotCreatedException when the name is too long
     */
    public LectureEntity newLecture(String name, String creatorName, Timestamp startTime,
                                    int frequency) throws LectureNotCreatedException {
        if (name.length() <= 255 && creatorName.length() <= 255) {
            LectureEntity n = new LectureEntity(name, creatorName, startTime);
            n.setFrequency(frequency);
            lectureRepository.save(n);
            return n;
        } else {
            throw new LectureNotCreatedException();
        }
    }

    /**
     * Deletes a lecture from the database.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int delete(UUID id, UUID modkey) throws LectureException, InvalidModkeyException {
        LectureEntity toDelete = getLectureById(id);
        if (toDelete == null) {
            throw new LectureNotFoundException();
        } else if (toDelete.getModkey().equals(modkey)) {
            lectureRepository.deleteById(id);
            return 0;
        }
        throw new InvalidModkeyException();
    }

    /**
     * Close a lecture.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int close(UUID id, UUID modkey) throws LectureException, InvalidModkeyException {
        LectureEntity toClose = getLectureById(id);
        if (toClose == null) {
            throw new LectureNotFoundException();
        } else if (toClose.getModkey().equals(modkey)) {
            toClose.close();
            lectureRepository.save(toClose);
            return 0;
        }
        throw new InvalidModkeyException();
    }

    /**
     * Checks if the provided moderator key matches the moderator key of the lecture.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int validateModerator(UUID id, UUID modkey)
            throws LectureException, InvalidModkeyException {
        LectureEntity l = getLectureById(id);
        if (l == null) {
            throw new LectureNotFoundException();
        } else if (l.getModkey().equals(modkey)) {
            return 0;
        }
        throw new InvalidModkeyException();
    }

    /**
     * Sets the frequency of asking questions of the lecture.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @param frequency the frequency of asking questions
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public int setFrequency(UUID id, UUID modkey, int frequency)
            throws LectureException, InvalidModkeyException {
        if (frequency < 0 || frequency > 300) {
            throw new LectureInvalidFrequencyException();
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(id);
        if (lecture == null) {
            throw new LectureNotFoundException();
        }
        if (lecture.getModkey().equals(modkey)) {
            lecture.setFrequency(frequency);
            lectureRepository.save(lecture);
            return 0;
        }
        throw new InvalidModkeyException();
    }
}
