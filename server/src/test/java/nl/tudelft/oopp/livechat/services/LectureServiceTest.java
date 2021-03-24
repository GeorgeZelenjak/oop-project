package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
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

    private final String longString = "a".repeat(256);
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());


    @Test
    void getLectureByIdSuccessfulTest() throws LectureException {
        LectureEntity lecture1 = new LectureEntity("name", "Codrin Socol", time);
        repository.save(lecture1);

        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertEquals(lecture1, lecture2);

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void getLectureByIdUnsuccessfulTest() throws LectureException {
        LectureEntity lecture1 = new LectureEntity("name", "Giulio Segalini", time);
        repository.save(lecture1);

        assertThrows(LectureNotFoundException.class,
                () -> lectureService.getLectureByIdNoModkey(UUID.randomUUID()));

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void newLectureTest() throws LectureException {
        LectureEntity lectureEntity = lectureService.newLecture("name", "Artjom Pugatsov", time);
        assertNotNull(lectureEntity);
        repository.deleteById(lectureEntity.getUuid());
    }

    @Test
    void newLectureLongCreatorTest() throws LectureException {
        assertThrows(LectureNotCreatedException.class, () -> lectureService.newLecture("name", longString, time));
    }

    @Test
    void newLectureLongNameTest() throws LectureException {
        assertThrows(LectureNotCreatedException.class, () -> lectureService.newLecture(longString,
                "Artjom Pugatsov", time));
    }

    @Test
    void deleteSuccessfulTest() throws Exception {
        LectureEntity lecture1 = new LectureEntity("name", "Tudor Popica", time);
        repository.save(lecture1);

        lectureService.delete(lecture1.getUuid(), lecture1.getModkey());
        assertThrows(LectureNotFoundException.class,
                () -> lectureService.getLectureByIdNoModkey(lecture1.getUuid()));
    }

    @Test
    void deleteUnsuccessfulTest() throws Exception {
        LectureEntity lecture1 = new LectureEntity("name", "Tudor Popica", time);
        repository.save(lecture1);

        assertThrows(InvalidModkeyException.class, () -> lectureService.delete(lecture1.getUuid(), UUID.randomUUID()));

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureTest() throws Exception {
        LectureEntity lecture1 = new LectureEntity("name", "creator_name", time);
        repository.save(lecture1);

        lectureService.close(lecture1.getUuid(), lecture1.getModkey());
        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertFalse(lecture2.isOpen());

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureUnsuccessfulTest() throws Exception {
        LectureEntity lecture1 = new LectureEntity("name", "creator_name", time);
        repository.save(lecture1);

        assertThrows(InvalidModkeyException.class, () -> lectureService.close(lecture1.getUuid(), UUID.randomUUID()));
        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertTrue(lecture2.isOpen());

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureNoLectureTest() throws Exception {
        LectureEntity lecture = new LectureEntity("name", "creator_name", time);

        assertThrows(LectureNotFoundException.class, () -> lectureService.close(lecture.getUuid(), lecture.getModkey()));
    }

    @Test
    void validateModeratorKeySuccessfulTest() throws Exception {
        LectureEntity lecture = new LectureEntity("name", "Jegor Zelenjak", time);
        repository.save(lecture);

        int res = lectureService.validateModerator(lecture.getUuid(), lecture.getModkey());
        assertEquals(0, res);

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void validateModeratorKeyUnsuccessfulTest() throws Exception {
        LectureEntity lecture = new LectureEntity("name", "Stefan Hugtenburg", time);
        repository.save(lecture);

        assertThrows(InvalidModkeyException.class, ()-> lectureService.validateModerator(lecture.getUuid(), UUID.randomUUID()));

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void validateModeratorKeyNoLectureTest() throws Exception {
        LectureEntity lecture = new LectureEntity("name", "Andy Zaidman", time);

        assertThrows(LectureNotFoundException.class, () -> lectureService.validateModerator(lecture.getUuid(), lecture.getModkey()));
    }

    @Test
    void getLectureByIdNotStartedTest() throws LectureException {
        LectureEntity lectureFuture = new LectureEntity("name", "Codrin Socol",
                new Timestamp(System.currentTimeMillis() + 0xFFFFFFL));
        repository.save(lectureFuture);

        assertThrows( LectureNotStartedException.class, () ->
            lectureService.getLectureByIdNoModkey(lectureFuture.getUuid()));
    }


}