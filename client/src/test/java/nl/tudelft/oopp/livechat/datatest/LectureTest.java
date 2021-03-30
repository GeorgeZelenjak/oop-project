package nl.tudelft.oopp.livechat.datatest;

import nl.tudelft.oopp.livechat.data.Lecture;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.UUID;

/**
 * Class for Lecture tests.
 */
public class LectureTest {

    private static Lecture lecture;
    private static final UUID lectureId = UUID.randomUUID();

    /**
     * Create a lecture before starting testing.
     */
    @BeforeAll
    public static void createLecture() {
        lecture = new Lecture(lectureId,null, "History of zebras", "Zebra");
        lecture.setFasterCount(42);
        lecture.setSlowerCount(69);
        Lecture.setCurrent(lecture);
    }

    @Test
    public void constructorTest() {
        assertNotNull(lecture);
    }

    @Test
    public void geUuidTest() {
        assertEquals(lectureId, lecture.getUuid());
    }

    @Test
    public void getModkeyExposedTest() {
        UUID modkey = UUID.randomUUID();
        Lecture lecture1 = new Lecture(lectureId, modkey, "History of zebras", "Zebra");
        assertEquals(modkey, lecture1.getModkey());
    }

    @Test
    public void getModkeyNotExposedTest() {
        assertNull(lecture.getModkey());
    }

    @Test
    public void setModkeyTest() {
        UUID modkey = UUID.randomUUID();
        Lecture lecture1 = new Lecture(lectureId, null, "History of zebras", "Zebra");
        lecture1.setModkey(modkey);

        assertEquals(modkey, lecture1.getModkey());
    }

    @Test
    public void getNameTest() {
        assertEquals("History of zebras", lecture.getName());
    }

    @Test
    public void getCreatorNameTest() {
        assertEquals("Zebra", lecture.getCreatorName());
    }

    @Test
    public void getFasterCountTest() {
        assertEquals(42, lecture.getFasterCount());
    }

    @Test
    public void setFasterCountTest() {
        lecture.setFasterCount(777);
        assertEquals(777, lecture.getFasterCount());

        //set it back for other tests
        lecture.setFasterCount(42);
    }

    @Test
    public void getSlowerCountTest() {
        assertEquals(69, lecture.getSlowerCount());
    }

    @Test
    public void setSlowerCountTest() {
        lecture.setSlowerCount(999);
        assertEquals(999, lecture.getSlowerCount());

        //set it back for other tests
        lecture.setSlowerCount(69);
    }

    @Test
    public void getFrequencyTest() {
        assertEquals(60, lecture.getFrequency());
    }

    @Test
    public void setFrequencyTest() {
        lecture.setFrequency(3000);
        assertEquals(3000, lecture.getFrequency());

        //set it back for other tests
        lecture.setFrequency(60);
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
    public void setOpenTest() {
        lecture.setOpen(false);
        assertFalse(lecture.isOpen());

        //set it back for other tests
        lecture.setOpen(true);
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
        if (!res.contains("Zebra")) {
            containsNeededInfo = false;
        }
        assertTrue(containsNeededInfo);
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(lecture, null);
    }

    @Test
    public void equalsSameTest() {
        assertEquals(lecture, lecture);
    }

    @Test
    public void equalsEqualTest() {
        Lecture lecture1 = new Lecture(lectureId, UUID.randomUUID(),
                "History of tomatoes", "Tomato");
        assertEquals(lecture, lecture1);
    }

    @Test
    public void equalsDifferentTest() {
        Lecture lecture1 = new Lecture(UUID.randomUUID(), null,
                "History of zebras", "Zebra");
        assertNotEquals(lecture, lecture1);
    }

    @Test
    public void hashCodeTest() {
        int hashCode = Objects.hash(lectureId, "History of zebras", "Zebra");
        assertEquals(hashCode, lecture.hashCode());
    }

    @Test
    public void getCurrentLectureTest() {
        assertEquals(lecture, Lecture.getCurrent());
    }

    @Test
    public void setCurrentLectureTest() {
        Lecture newLecture = new Lecture();
        Lecture.setCurrent(newLecture);
        assertEquals(newLecture, Lecture.getCurrent());

        //set it back for other tests
        Lecture.setCurrent(lecture);
    }
}
