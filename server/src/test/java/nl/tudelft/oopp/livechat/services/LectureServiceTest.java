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

    private String longString = "a".repeat(256);


    @Test
    void getLectureByIdSuccessfulTest() {
        LectureEntity lecture1 = new LectureEntity("name", "Codrin Socol");
        repository.save(lecture1);

        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertEquals(lecture1, lecture2);

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void getLectureByIdUnsuccessfulTest() {
        LectureEntity lecture1 = new LectureEntity("name", "Giulio Segalini");
        repository.save(lecture1);

        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(UUID.randomUUID());
        assertNull(lecture2);

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void newLectureTest() {
        LectureEntity lectureEntity = lectureService.newLecture("name", "Artjom Pugatsov");
        assertNotNull(lectureEntity);
        repository.deleteById(lectureEntity.getUuid());
    }

    @Test
    void newLectureLongCreatorTest() {
        LectureEntity lectureEntity = lectureService.newLecture("name", longString);
        assertNull(lectureEntity);
    }

    @Test
    void newLectureLongNameTest() {
        LectureEntity lectureEntity = lectureService.newLecture(longString, "Artjom Pugatsov");
        assertNull(lectureEntity);
    }

    @Test
    void deleteSuccessfulTest() {
        LectureEntity lecture1 = new LectureEntity("name", "Tudor Popica");
        repository.save(lecture1);

        lectureService.delete(lecture1.getUuid(), lecture1.getModkey());

        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertNull(lecture2);
    }

    @Test
    void deleteUnsuccessfulTest() {
        LectureEntity lecture1 = new LectureEntity("name", "Tudor Popica");
        repository.save(lecture1);

        lectureService.delete(lecture1.getUuid(), UUID.randomUUID());

        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertNotNull(lecture2);

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureTest() {
        LectureEntity lecture1 = new LectureEntity("name", "creator_name");
        repository.save(lecture1);

        lectureService.close(lecture1.getUuid(), lecture1.getModkey());
        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertFalse(lecture2.isOpen());

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureUnsuccessfulTest() {
        LectureEntity lecture1 = new LectureEntity("name", "creator_name");
        repository.save(lecture1);

        lectureService.close(lecture1.getUuid(), UUID.randomUUID());
        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertTrue(lecture2.isOpen());

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureNoLectureTest() {
        LectureEntity lecture = new LectureEntity("name", "creator_name");

        int result = lectureService.close(lecture.getUuid(), lecture.getModkey());
        assertEquals(-1, result);
    }

    @Test
    void validateModeratorKeySuccessfulTest() {
        LectureEntity lecture = new LectureEntity("name", "Jegor Zelenjak");
        repository.save(lecture);

        int res = lectureService.validateModerator(lecture.getUuid(), lecture.getModkey());
        assertEquals(0, res);

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void validateModeratorKeyUnsuccessfulTest() {
        LectureEntity lecture = new LectureEntity("name", "Stefan Hugtenburg");
        repository.save(lecture);

        int res = lectureService.validateModerator(lecture.getUuid(), UUID.randomUUID());
        assertEquals(-1, res);

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void validateModeratorKeyNoLectureTest() {
        LectureEntity lecture = new LectureEntity("name", "Andy Zaidman");

        int res = lectureService.validateModerator(lecture.getUuid(), lecture.getModkey());
        assertEquals(-1, res);
    }

}