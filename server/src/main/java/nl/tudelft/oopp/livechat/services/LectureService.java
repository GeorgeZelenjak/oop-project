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
     * Gets lecture by id.
     *
     * @param id the id of the lecture
     * @return the lecture if the id is found in the database
     */
    public LectureEntity getLectureByIdNoModkey(UUID id) throws LectureException {
        LectureEntity toSend =  lectureRepository.findLectureEntityByUuid(id);
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
     * Gets lecture by id.
     * @param id the id
     * @return the lecture
     */
    private LectureEntity getLectureById(UUID id) {
        return lectureRepository.findLectureEntityByUuid(id);
    }

    /**
     * Creates a new lecture in the database.
     *
     * @param name        the name of the lecture
     * @param creatorName the name of the creator
     * @param startTime   the start time
     * @return the new lecture entity
     */
    public LectureEntity newLecture(String name, String creatorName, Timestamp startTime, int frequency)
            throws LectureNotCreatedException {
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
     * Deletes a lecture if the moderator key is found in the database.
     *
     * @param id     the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful
     */
    public int delete(UUID id, UUID modkey)
            throws LectureException, InvalidModkeyException {
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
     * Close a lecture for future uses.
     *
     * @param id     the lecture id
     * @param modkey the modkey
     * @return 0 if successful, -1 otherwise
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
     * Checks if moderator.
     *
     * @param id     the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful, -1 otherwise
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
}
