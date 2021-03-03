package nl.tudelft.oopp.lecture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LectureTest {

    Lecture t1;
    Lecture t2;
    Lecture t3;
    Lecture t4;

    @BeforeEach
    void setUp() {
        t1 = new Lecture("test1", "prof1");
        t2 = new Lecture("test2", "prof2");
        t3 = new Lecture("test1", "prof2");
        t4 = new Lecture("test2", "prof1");
    }

    @Test
    void getName() {
        assertEquals("test1", t1.getName());
        assertEquals("test2", t2.getName());
    }

    @Test
    void getCreatorName() {
        assertEquals("prof1", t1.getCreatorName());
        assertEquals("prof2", t2.getCreatorName());
    }

    @Test
    void getFasterCount() {
        assertEquals(0, t1.getFasterCount());
    }

    @Test
    void getSlowerCount() {
        assertEquals(0, t1.getFasterCount());
    }


    @Test
    void getFrequency() {
        assertEquals(60, t1.getFrequency());
    }

    @Test
    void setName() {
        t1.setName("liaccoloz");
        assertEquals("liaccoloz", t1.getName());
    }

    @Test
    void setFrequency() {
        t1.setFrequency(420);
        assertEquals(420, t1.getFrequency());
    }

    @Test
    void close() {
        assertTrue(t1.isOpen());
        t1.close();
        assertFalse(t1.isOpen());
    }

    @Test
    void reOpen() {
        assertTrue(t1.isOpen());
        t1.close();
        assertFalse(t1.isOpen());
        t1.reOpen();
        assertTrue(t1.isOpen());
    }

}