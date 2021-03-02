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

    public LectureEntity getLectureById(String id) {
        UUID ID = UUID.fromString(id);
        return lectureRepository.findLectureEntityByUuid(ID);
    }

    /**
     * Method that creates a new general lecture in the DB.
     */
    public LectureEntity newLecture(String name, String creatorName, LocalDateTime startTime) {
        LectureEntity n = new LectureEntity(name, creatorName, startTime);
        lectureRepository.save(n);
        return n;
    }

    public int delete(String id, String modkey) {
        UUID ID = UUID.fromString(id);
        LectureEntity toDelete = getLectureById(id);
        UUID modk = UUID.fromString(modkey);
        if (toDelete != null && toDelete.getModkey().equals(modk)) {
            lectureRepository.deleteById(ID);
            return 0;
        }
        return -1;
    }


}
