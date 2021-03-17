package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Class for Lecture entity tests.
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class LectureEntityTest {
    private static LectureEntity lectureEntity;
    private static final UUID modkey = UUID.randomUUID();
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    /**
     * Sets up a lecture before starting testing.
     */
    @BeforeAll
    static void setUp() {
        lectureEntity = new LectureEntity("Algorithms and Data Structures: Red-black trees",
                "Ivo van Kreveld", time);
        lectureEntity.setModkey(modkey);
        lectureEntity.incrementSlowerCount();
        lectureEntity.incrementFasterCount();
    }

    @Test
    void constructorTest() {
        assertNotNull(lectureEntity);
    }

    @Test
    void staticConstructorTest() {
        assertNotNull(LectureEntity.create("Algorithms and Data Structures: Red-black trees",
                "Ivo van Kreveld", new Timestamp(System.currentTimeMillis())));
    }

    @Test
    void getUuidTest() {
        assertNotNull(lectureEntity.getUuid());
    }

    @Test
    void getModkeyTest() {
        assertEquals(modkey, lectureEntity.getModkey());
    }

    @Test
    void setModkeyTest() {
        UUID newModkey = UUID.randomUUID();
        lectureEntity.setModkey(newModkey);
        assertEquals(newModkey, lectureEntity.getModkey());
    }

    @Test
    void getNameTest() {
        String expected = "Algorithms and Data Structures: Red-black trees";
        assertEquals(expected, lectureEntity.getName());
    }

    @Test
    void getCreatorNameTest() {
        String expected = "Ivo van Kreveld";
        assertEquals(expected, lectureEntity.getCreatorName());
    }

    @Test
    void getFasterCountTest() {
        assertEquals(1, lectureEntity.getFasterCount());
    }

    @Test
    void getSlowerCountTest() {
        assertEquals(1, lectureEntity.getSlowerCount());
    }

    @Test
    void getFrequencyTest() {
        assertEquals(60, lectureEntity.getFrequency());
    }

    @Test
    void getStartTimeTest() {
        assertNotNull(lectureEntity.getStartTime());
    }

    @Test
    void setStartTimeTest() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        lectureEntity.setStartTime(time);
        assertEquals(time, lectureEntity.getStartTime());
    }

    @Test
    void setNameTest() {
        String newName = "Algorithms and Data Structures: AVL trees";
        lectureEntity.setName(newName);
        assertEquals("Algorithms and Data Structures: AVL trees", lectureEntity.getName());
    }

    @Test
    void setFrequencyTest() {
        lectureEntity.setFrequency(100);
        assertEquals(100, lectureEntity.getFrequency());
    }

    @Test
    void incrementFasterCountTest() {
        int oldFaster = lectureEntity.getFasterCount();
        int oldSlower = lectureEntity.getSlowerCount();
        lectureEntity.incrementFasterCount();
        assertTrue(lectureEntity.getFasterCount() == oldFaster + 1
                && oldSlower == lectureEntity.getSlowerCount());
    }

    @Test
    void incrementSlowerCountTest() {
        int oldFaster = lectureEntity.getFasterCount();
        int oldSlower = lectureEntity.getSlowerCount();
        lectureEntity.incrementSlowerCount();
        assertTrue(lectureEntity.getSlowerCount() == oldSlower + 1
                && lectureEntity.getFasterCount() == oldFaster);
    }

    @Test
    void resetSpeedCountsTest() {
        lectureEntity.incrementFasterCount();
        lectureEntity.incrementSlowerCount();
        lectureEntity.resetSpeedCounts();
        assertTrue(lectureEntity.getSlowerCount() == 0
                && lectureEntity.getFasterCount() == 0);
    }

    @Test
    void isOpenTest() {
        assertTrue(lectureEntity.isOpen());
    }

    @Test
    void reopenTest() {
        lectureEntity.close();
        lectureEntity.reOpen();
        assertTrue(lectureEntity.isOpen());
    }

    @Test
    void closeTest() {
        lectureEntity.close();
        assertFalse(lectureEntity.isOpen());

        //set back for other tests
        lectureEntity.reOpen();
    }


    @Test
    void equalsNullTest() {
        assertNotEquals(lectureEntity, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(lectureEntity, lectureEntity);
    }

    @Test
    void equalsDifferentTest() {
        LectureEntity other = new LectureEntity("Algorithms and Data Structures: Red-black trees",
                "Ivo van Kreveld", time);
        assertNotEquals(lectureEntity, other);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(lectureEntity.getUuid(), lectureEntity.getModkey(),
                lectureEntity.getName(),
                lectureEntity.getCreatorName(),
                lectureEntity.getStartTime());
        assertEquals(hash, lectureEntity.hashCode());
    }
}
