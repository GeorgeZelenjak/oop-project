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
        LectureEntity lecture1 = new LectureEntity("Names", "Jegor", time);
        repository.save(lecture1);

        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertEquals(lecture1, lecture2);

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void getLectureByIdUnsuccessfulTest() {
        LectureEntity lecture1 = new LectureEntity("Names", "Cərciz", time);
        repository.save(lecture1);

        assertThrows(LectureNotFoundException.class, () ->
                lectureService.getLectureByIdNoModkey(UUID.randomUUID()));

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void newLectureTest() throws LectureException {
        LectureEntity lectureEntity = lectureService.newLecture("Names", "Georgius",
                time, 10);
        assertNotNull(lectureEntity);
        repository.deleteById(lectureEntity.getUuid());
    }

    @Test
    void newLectureLongCreatorTest() {
        assertThrows(LectureNotCreatedException.class, () ->
                lectureService.newLecture("Names", longString, time, 10));
    }

    @Test
    void newLectureLongNameTest() {
        assertThrows(LectureNotCreatedException.class, () -> lectureService.newLecture(longString,
                "Georg", time, 10));
    }

    @Test
    void deleteSuccessfulTest() throws Exception {
        LectureEntity lecture1 = new LectureEntity("Names", "Georges", time);
        repository.save(lecture1);

        lectureService.delete(lecture1.getUuid(), lecture1.getModkey());
        assertThrows(LectureNotFoundException.class, () ->
                lectureService.getLectureByIdNoModkey(lecture1.getUuid()));
    }

    @Test
    void deleteUnsuccessfulTest() {
        LectureEntity lecture1 = new LectureEntity("Names", "Gheorghe", time);
        repository.save(lecture1);

        assertThrows(InvalidModkeyException.class, () ->
                lectureService.delete(lecture1.getUuid(), UUID.randomUUID()));

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureTest() throws Exception {
        LectureEntity lecture1 = new LectureEntity("Names", "Jiří", time);
        repository.save(lecture1);

        lectureService.close(lecture1.getUuid(), lecture1.getModkey());
        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertFalse(lecture2.isOpen());

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureUnsuccessfulTest() throws Exception {
        LectureEntity lecture1 = new LectureEntity("Names", "György", time);
        repository.save(lecture1);

        assertThrows(InvalidModkeyException.class, () ->
                lectureService.close(lecture1.getUuid(), UUID.randomUUID()));
        LectureEntity lecture2 = lectureService.getLectureByIdNoModkey(lecture1.getUuid());
        assertTrue(lecture2.isOpen());

        repository.deleteById(lecture1.getUuid());
    }

    @Test
    void closeLectureNoLectureTest() {
        LectureEntity lecture = new LectureEntity("Names", "Jerzy", time);

        assertThrows(LectureNotFoundException.class, () ->
                lectureService.close(lecture.getUuid(), lecture.getModkey()));
    }

    @Test
    void validateModeratorKeySuccessfulTest() throws Exception {
        LectureEntity lecture = new LectureEntity("Names", "Egor", time);
        repository.save(lecture);

        int res = lectureService.validateModerator(lecture.getUuid(), lecture.getModkey());
        assertEquals(0, res);

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void validateModeratorKeyUnsuccessfulTest() {
        LectureEntity lecture = new LectureEntity("Names", "Jorge", time);
        repository.save(lecture);

        assertThrows(InvalidModkeyException.class, () ->
                lectureService.validateModerator(lecture.getUuid(), UUID.randomUUID()));

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void validateModeratorKeyNoLectureTest() {
        LectureEntity lecture = new LectureEntity("Names", "Giorgio", time);

        assertThrows(LectureNotFoundException.class, () ->
                lectureService.validateModerator(lecture.getUuid(), lecture.getModkey()));
    }

    @Test
    void setFrequencySuccessfulTest() throws Exception {
        LectureEntity lecture = new LectureEntity("Names", "Yrjö", time);
        lecture.setFrequency(42);
        repository.save(lecture);

        assertEquals(0, lectureService.setFrequency(lecture.getUuid(),
                lecture.getModkey(), 60));
        LectureEntity l = repository.findLectureEntityByUuid(lecture.getUuid());
        assertNotNull(l);
        assertEquals(60, l.getFrequency());

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void setFrequencySuccessfulLowerBoundaryTest() throws Exception {
        LectureEntity lecture = new LectureEntity("Names", "Jeg", time);
        lecture.setFrequency(42);
        repository.save(lecture);

        assertEquals(0, lectureService.setFrequency(lecture.getUuid(),
                lecture.getModkey(), 0));
        LectureEntity l = repository.findLectureEntityByUuid(lecture.getUuid());
        assertNotNull(l);
        assertEquals(0, l.getFrequency());

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void setFrequencySuccessfulUpperBoundaryTest() throws Exception {
        LectureEntity lecture = new LectureEntity("Names", "Jegorka", time);
        lecture.setFrequency(42);
        repository.save(lecture);

        assertEquals(0, lectureService.setFrequency(lecture.getUuid(),
                lecture.getModkey(), 300));
        LectureEntity l = repository.findLectureEntityByUuid(lecture.getUuid());
        assertNotNull(l);
        assertEquals(300, l.getFrequency());

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void setFrequencyNegativeFrequencyTest() {
        LectureEntity lecture = new LectureEntity("Names", "Yegor", time);
        lecture.setFrequency(42);
        repository.save(lecture);

        assertThrows(LectureInvalidFrequencyException.class, () ->
                lectureService.setFrequency(lecture.getUuid(), lecture.getModkey(), -1));

        LectureEntity l = repository.findLectureEntityByUuid(lecture.getUuid());
        assertNotNull(l);
        assertEquals(42, l.getFrequency());

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void setFrequencyTooLargeFrequencyTest() {
        LectureEntity lecture = new LectureEntity("Names", "Yegor", time);
        lecture.setFrequency(42);
        repository.save(lecture);

        assertThrows(LectureInvalidFrequencyException.class, () ->
                lectureService.setFrequency(lecture.getUuid(), lecture.getModkey(), 301));

        LectureEntity l = repository.findLectureEntityByUuid(lecture.getUuid());
        assertNotNull(l);
        assertEquals(42, l.getFrequency());

        repository.deleteById(lecture.getUuid());
    }

    @Test
    void setFrequencyNoLectureTest() {
        LectureEntity lecture = new LectureEntity("Names", "Jegorkin", time);

        assertThrows(LectureNotFoundException.class, () ->
                lectureService.setFrequency(lecture.getUuid(), lecture.getModkey(), 44));
    }

    @Test
    void setFrequencyInvalidModKeyTest() {
        LectureEntity lecture = new LectureEntity("Names", "Jegoridze", time);
        lecture.setFrequency(42);
        repository.save(lecture);

        assertThrows(InvalidModkeyException.class, () ->
                lectureService.setFrequency(lecture.getUuid(), UUID.randomUUID(), 228));

        LectureEntity l = repository.findLectureEntityByUuid(lecture.getUuid());
        assertNotNull(l);
        assertEquals(42, l.getFrequency());

        repository.deleteById(lecture.getUuid());
    }
}