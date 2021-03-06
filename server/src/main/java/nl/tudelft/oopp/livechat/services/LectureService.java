package nl.tudelft.oopp.livechat.services;

import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.springframework.stereotype.Service;


@Service
public class LectureService {

    final LectureRepository lectureRepository;

    /**.
     * Constructor for the lecture service.
     * @param lectureRepository lecture repository
     */
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    /**.
     * Gets lecture by id.
     * @param id the id of the lecture
     * @return the lecture if the id is found in the database
     */
    public LectureEntity getLectureById(String id) {
        UUID uuid = UUID.fromString(id);
        return lectureRepository.findLectureEntityByUuid(uuid);
    }

    /**.
     * Creates a new lecture in the database.
     * @param name the name of the lecture
     * @param creatorName the name of the creator
     * @return the new lecture entity
     */
    public LectureEntity newLecture(String name, String creatorName) {
        LectureEntity n = new LectureEntity(name, creatorName);
        lectureRepository.save(n);
        return n;
    }

    /**.
     * Deletes a lecture if the moderator key is found in the database.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful, -1 otherwise
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
