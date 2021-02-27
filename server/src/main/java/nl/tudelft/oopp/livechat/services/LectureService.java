package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectureService {

    @Autowired
    LectureRepository repository;

    public LectureEntity getLectureById(String id) {
        return repository.findLectureEntityByUuid(id);
    }

    public LectureEntity newLecture(){
        LectureEntity n = new LectureEntity();
        repository.save(n);
        return n;
    }

}
