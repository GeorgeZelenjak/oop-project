package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.Lecture;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class LectureTest {

    static Lecture lecture;

    @BeforeAll
    public static void createLecture() {
        lecture = new Lecture(UUID.randomUUID(),null, "History of zebras", "Yours truly");
    }

    @Test
    public void geUuidTest() {
        assertNotNull(lecture.getUuid());
    }

    @Test
    public void getModkeyTest() {
        assertNull(lecture.getModkey());
    }

    @Test
    public void getNameTest() {
        assertEquals("History of zebras", lecture.getName());
    }

    @Test
    public void getCreatorNameTest() {
        assertEquals("Yours truly", lecture.getCreatorName());
    }

    @Test
    public void getFasterCountTest() {
        assertEquals(0, lecture.getFasterCount());
    }

    @Test
    public void getSlowerCountTest() {
        assertEquals(0, lecture.getSlowerCount());
    }

    @Test
    public void getFrequencyTest() {
        assertEquals(60, lecture.getFrequency());
    }

    @Test
    public void getStartTimeTest() {
        assertNotNull(lecture.getStartTime());
    }

    @Test
    public void isOpenTest() {
        assertTrue(lecture.isOpen());
    }

    @Test
    public void setAndGetCurrentLectureTest() {
        Lecture.setCurrentLecture(lecture);
        assertEquals(lecture, Lecture.getCurrentLecture());
    }

    @Test
    public void toStringTest() {
        boolean containsNeededInfo = true;
        String res = lecture.toString();
        if (!res.contains(lecture.getUuid().toString())) {
            containsNeededInfo = false;
        }
        if (!res.contains("History of zebras")) {
            containsNeededInfo = false;
        }
        if (!res.contains("Yours truly")) {
            containsNeededInfo = false;
        }
        assertTrue(containsNeededInfo);
    }


}
