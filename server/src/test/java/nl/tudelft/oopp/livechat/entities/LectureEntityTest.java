package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.util.Objects;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class LectureEntityTest {
    private static LocalDateTime time = LocalDateTime.of(2020, 12, 31, 10, 0, 0, 0);
    private static LectureEntity lectureEntity;

    @BeforeAll
    static void setUp() {
        lectureEntity = new LectureEntity("Algorithms and Data Structures: Red-black trees", "Ivo van Kreveld", time);
    }

    @Test
    void constructorTest() {
        assertNotNull(lectureEntity);
    }

    @Test
    void getUuidTest() {
        //cannot test the uuid, since I have no access to it and it is generated randomly
        assertNotNull(lectureEntity.getUuid());
    }

    @Test
    void getModkeyTest() {
        //cannot test the modkey (uuid), since I have no access to it and it is generated randomly
        assertNotNull(lectureEntity.getModkey());
    }

    @Test
    void getNameTest() {
        String expected = new String("Algorithms and Data Structures: Red-black trees");
        assertEquals(expected, lectureEntity.getName());
    }

    @Test
    void getCreatorNameTest() {
        String expected = new String("Ivo van Kreveld");
        assertEquals(expected, lectureEntity.getCreatorName());
    }

    @Test
    void getFasterCountTest() {
        assertEquals(0, lectureEntity.getFasterCount());
    }

    @Test
    void getSlowerCountTest() {
        assertEquals(0, lectureEntity.getSlowerCount());
    }

    @Test
    void getStartTimeTest() {
        assertEquals(time, lectureEntity.getStartTime());
    }

    @Test
    void getFrequencyTest() {
        assertEquals(60, lectureEntity.getFrequency());
    }

    @Test
    void setNameTest() {
        String newName = new String("Algorithms and Data Structures: AVL trees");
        lectureEntity.setName(newName);
        assertEquals(new String("Algorithms and Data Structures: AVL trees"), lectureEntity.getName());
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
        assertTrue(lectureEntity.getFasterCount() == oldFaster+1 && oldSlower == lectureEntity.getSlowerCount());
    }

    @Test
    void incrementSlowerCountTest() {
        int oldFaster = lectureEntity.getFasterCount();
        int oldSlower = lectureEntity.getSlowerCount();
        lectureEntity.incrementSlowerCount();
        assertTrue(lectureEntity.getSlowerCount() == oldSlower + 1 && lectureEntity.getFasterCount() == oldFaster);
    }

    @Test
    void resetSpeedCountsTest() {
        lectureEntity.incrementFasterCount();
        lectureEntity.incrementSlowerCount();
        lectureEntity.resetSpeedCounts();
        assertTrue(lectureEntity.getSlowerCount() == 0 && lectureEntity.getFasterCount() == 0);
    }

    @Test
    void testEqualsNullTest() {
        assertNotEquals(lectureEntity, null);
    }

    @Test
    void testEqualsSameTest() {
        assertEquals(lectureEntity, lectureEntity);
    }

    //cannot test equals with different object, because I have no access to the uuid, which is randomly generated

    @Test
    void testEqualsDifferentTest() {
        LectureEntity other = new LectureEntity("Algorithms and Data Structures: Red-black trees", "Ivo van Kreveld", time);
        assertNotEquals(lectureEntity, other);
    }

    @Test
    void testHashCodeTest() {
        int hash = Objects.hash(lectureEntity.getUuid(), lectureEntity.getModkey(),
                lectureEntity.getName(), lectureEntity.getCreatorName(), lectureEntity.getStartTime());
        assertEquals(hash, lectureEntity.hashCode());
    }
}
