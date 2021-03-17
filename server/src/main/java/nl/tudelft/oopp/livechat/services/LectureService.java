package nl.tudelft.oopp.livechat.services;

import java.sql.Timestamp;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.springframework.stereotype.Service;


/**
 * Class for the Lecture service.
 */
@Service
public class LectureService {

    final LectureRepository lectureRepository;

    /**
     * Constructor for the lecture service.
     * @param lectureRepository lecture repository
     */
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    /**
     * Gets lecture by id.
     * @param id the id of the lecture
     * @return the lecture if the id is found in the database
     */
    public LectureEntity getLectureByIdNoModkey(UUID id) {
        LectureEntity toSend =  lectureRepository.findLectureEntityByUuid(id);
        if (toSend == null) {
            return null;
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
     * @param name the name of the lecture
     * @param creatorName the name of the creator
     * @return the new lecture entity
     */
    public LectureEntity newLecture(String name, String creatorName, Timestamp startTime) {
        if (name.length() <= 255 && creatorName.length() <= 255) {
            LectureEntity n = new LectureEntity(name, creatorName, startTime);
            lectureRepository.save(n);
            return n;
        } else {
            return null;
        }
    }

    /**
     * Deletes a lecture if the moderator key is found in the database.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful, -1 otherwise
     */
    public int delete(UUID id, UUID modkey) {
        LectureEntity toDelete = getLectureById(id);
        if (toDelete != null && toDelete.getModkey().equals(modkey)) {
            lectureRepository.deleteById(id);
            return 0;
        }
        return -1;
    }

    /**
     * Close a lecture for future uses.
     * @param id the lecture id
     * @param modkey the modkey
     * @return 0 if successful, -1 otherwise
     */
    public int close(UUID id, UUID modkey) {
        LectureEntity toClose = getLectureById(id);
        if (toClose != null && toClose.getModkey().equals(modkey)) {
            toClose.close();
            lectureRepository.save(toClose);
            return 0;
        }
        return -1;
    }

    /**
     * Checks if moderator.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful, -1 otherwise
     */
    public int validateModerator(UUID id, UUID modkey) {
        LectureEntity l = getLectureById(id);
        if (l != null && l.getModkey().equals(modkey)) {
            return 0;
        }
        return -1;
    }
}
