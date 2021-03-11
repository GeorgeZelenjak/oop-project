package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class LectureServiceTest {

    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureRepository repository;


    @Test
    void getLectureByIdSuccessfulTest() {
        LectureEntity l = new LectureEntity("name", "Codrin Socol");
        repository.save(l);

        LectureEntity m = lectureService.getLectureById(l.getUuid());
        assertEquals(l, m);
    }

    @Test
    void getLectureByIdUnsuccessfulTest() {
        LectureEntity l = new LectureEntity("name", "Giulio Segalini");
        repository.save(l);

        LectureEntity m = lectureService.getLectureById(UUID.randomUUID());
        assertNull(m);
    }

    @Test
    void newLectureTest() {
        assertNotNull(lectureService.newLecture("name", "Artjom Pugatsov"));
    }

    @Test
    void deleteSuccessfulTest() {
        LectureEntity l = new LectureEntity("name", "Tudor Popica");
        repository.save(l);

        lectureService.delete(l.getUuid(), l.getModkey());

        LectureEntity m = lectureService.getLectureById(l.getUuid());
        assertNull(m);
    }

    @Test
    void deleteUnsuccessfulTest() {
        LectureEntity l = new LectureEntity("name", "Oleg Danilov");
        repository.save(l);

        lectureService.delete(l.getUuid(), UUID.randomUUID());

        LectureEntity m = lectureService.getLectureById(l.getUuid());
        assertNotNull(m);
    }

    @Test
    void validateModeratorKeySuccessfulTest() {
        LectureEntity l = new LectureEntity("name", "Jegor Zelenjak");
        repository.save(l);

        int res = lectureService.validateModerator(l.getUuid(), l.getModkey());
        assertEquals(0, res);
    }

    @Test
    void validateModeratorKeyUnsuccessfulTest() {
        LectureEntity l = new LectureEntity("name", "Stefan Hugtenburg");
        repository.save(l);

        int res = lectureService.validateModerator(l.getUuid(), UUID.randomUUID());
        assertEquals(-1, res);
    }

    @Test
    void validateModeratorKeyNoLectureTest() {
        LectureEntity l = new LectureEntity("name", "Andy Zaidman");
        repository.save(l);

        lectureService.delete(l.getUuid(), l.getModkey());

        int res = lectureService.validateModerator(l.getUuid(), l.getModkey());
        assertEquals(-1, res);
    }
}