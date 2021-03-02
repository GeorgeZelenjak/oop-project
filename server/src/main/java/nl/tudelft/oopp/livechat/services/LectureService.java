package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class LectureService {

    @Autowired
    LectureRepository lectureRepository;

    public LectureEntity getLectureById(String id) {
        return lectureRepository.findLectureEntityByUuid(id);
    }

    /**
     * Method that creates a new general lecture in the DB.
     */
    public LectureEntity newLecture(String name, String creator_name, LocalDateTime startTime) {
        LectureEntity n = new LectureEntity(name, creator_name, startTime);
        lectureRepository.save(n);
        return n;
    }

    public void delete(String id) {
        lectureRepository.deleteById(id);
    }


}
