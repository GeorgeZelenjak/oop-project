package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class LectureEntityTest {
    private static LectureEntity lectureEntity;

    @BeforeAll
    static void setUp() {
        lectureEntity = new LectureEntity("Algorithms and Data Structures: Red-black trees",
                "Ivo van Kreveld");
    }

    @Test
    void constructorTest() {
        assertNotNull(lectureEntity);
    }

    @Test
    void staticConstructorTest() {
        assertNotNull(LectureEntity.create("Algorithms and Data Structures: Red-black trees",
                "Ivo van Kreveld"));
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
        assertEquals(0, lectureEntity.getFasterCount());
    }

    @Test
    void getSlowerCountTest() {
        assertEquals(0, lectureEntity.getSlowerCount());
    }

    @Test
    void getFrequencyTest() {
        assertEquals(60, lectureEntity.getFrequency());
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
    void testEqualsNullTest() {
        assertNotEquals(lectureEntity, null);
    }

    @Test
    void testEqualsSameTest() {
        assertEquals(lectureEntity, lectureEntity);
    }

    @Test
    void testEqualsDifferentTest() {
        LectureEntity other = new LectureEntity("Algorithms and Data Structures: Red-black trees",
                "Ivo van Kreveld");
        assertNotEquals(lectureEntity, other);
    }

    @Test
    void testHashCodeTest() {
        int hash = Objects.hash(lectureEntity.getUuid(), lectureEntity.getModkey(),
                lectureEntity.getName(),
                lectureEntity.getCreatorName(),
                lectureEntity.getStartTime());
        assertEquals(hash, lectureEntity.hashCode());
    }

    @Test
    void reOpenTest() {
        lectureEntity.close();
        lectureEntity.reOpen();
        assertTrue(lectureEntity.isOpen());
    }
}
