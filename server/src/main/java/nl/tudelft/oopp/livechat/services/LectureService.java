package nl.tudelft.oopp.livechat.services;

import java.time.LocalDateTime;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LectureService {

    @Autowired
    LectureRepository lectureRepository;

    /**
     * Gets lecture by id.
     *
     * @param id the id
     * @return the lecture by id
     */
    public LectureEntity getLectureById(String id) {
        UUID uuid = UUID.fromString(id);
        return lectureRepository.findLectureEntityByUuid(uuid);
    }

    /**
     * Method that creates a new general lecture in the DB.
     *
     * @param name        the name
     * @param creatorName the creator name
     * @return the lecture entity
     */
    public LectureEntity newLecture(String name, String creatorName) {
        LectureEntity n = new LectureEntity(name, creatorName);
        lectureRepository.save(n);
        return n;
    }

    /**
     * Delete a lecture if correct modkey is supplied.
     *
     * @param id     the id
     * @param modkey the modkey
     * @return 0 if success, -1 otherwise
     */
    public int delete(String id, String modkey) {
        UUID uuid = UUID.fromString(id);
        LectureEntity toDelete = getLectureById(id);
        UUID modk = UUID.fromString(modkey);
        if (toDelete != null && toDelete.getModkey().equals(modk)) {
            lectureRepository.deleteById(uuid);
            return 0;
        }
        return -1;
    }


}
