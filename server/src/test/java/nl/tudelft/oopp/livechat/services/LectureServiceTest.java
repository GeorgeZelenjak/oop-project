package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class LectureServiceTest {

    @Autowired
    LectureService lectureService;

    @Autowired
    private LectureRepository repository;

    @Test
    void getLectureById() {
        LectureEntity l = new LectureEntity("name", "creator_name", LocalDateTime.now());
        repository.save(l);
        LectureEntity m = lectureService.getLectureById(l.getUuid());
        assertEquals(l, m);
    }

    @Test
    void newLecture() {
        assertNotNull(lectureService
                .newLecture("name", "creator_name", LocalDateTime.now().withNano(0)));
    }

    @Test
    void delete() {
        LectureEntity l = new LectureEntity("name", "creator_name", LocalDateTime.now());
        repository.save(l);
        lectureService.delete(l.getUuid());
        LectureEntity m = lectureService.getLectureById(l.getUuid());
        assertNull(m);

    }
}