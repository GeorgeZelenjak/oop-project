package nl.tudelft.oopp.demo.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.tudelft.oopp.demo.data.Lecture;
import org.junit.jupiter.api.Test;


public class ServerCommunicationTest {

    @Test
    public void TestCreateLectureNotNull() {
        assertNotNull(ServerCommunication.createLecture("name"));
    }
    @Test
    public void TestLectureNameMatches() {
       Lecture res = ServerCommunication.createLecture("name");
       assertEquals("name", res.getName());
    }
}
