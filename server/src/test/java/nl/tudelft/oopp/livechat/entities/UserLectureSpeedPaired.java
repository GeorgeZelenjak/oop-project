package nl.tudelft.oopp.livechat.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class UserLectureSpeedPaired {


    private static UserLectureSpeedTable speedVote;
    private static UUID uuid;

    /**
     * Sets up everything.
     */
    @BeforeAll
    public static void setUp() {
        uuid = UUID.randomUUID();
        speedVote = new UserLectureSpeedTable(11, uuid, "faster");
    }

    @Test
    public void emptyConstructorTest() {
        UserLectureSpeedTable speedVote1 = new UserLectureSpeedTable();
        assertNotNull(speedVote1);
    }

    @Test
    public void nonEmptyConstructorTest() {
        UserLectureSpeedTable speedVote1 = new UserLectureSpeedTable(11, uuid, "faster");
        assertNotNull(speedVote1);
    }

    @Test
    public void getUidTest() {
        assertEquals(11, speedVote.getUid());
    }

    @Test
    public void setUidTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(11, uuid, "faster");
        speedVoteNew.setUid(12);
        assertEquals(12, speedVoteNew.getUid());
    }

    @Test
    public void getUuidTest() {
        assertEquals(uuid, speedVote.getUuid());
    }

    @Test
    public void setUuidTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(11, uuid, "faster");
        UUID newUuid = UUID.randomUUID();
        speedVoteNew.setUuid(newUuid);
        assertEquals(newUuid, speedVoteNew.getUuid());
    }

    @Test
    public void getVoteOnLectureSpeedTest() {
        assertEquals("faster", speedVote.getVoteOnLectureSpeed());
    }

    @Test
    public void setVoteOnLectureSpeedTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(11, uuid, "faster");
        speedVoteNew.setVoteOnLectureSpeed("slower");
        assertEquals("slower", speedVoteNew.getVoteOnLectureSpeed());
    }

    @Test
    public void equalsSimilarTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(11, uuid, "faster");
        assertEquals(speedVoteNew, speedVote);
    }

    @Test
    public void equalsSameTest() {
        assertEquals(speedVote, speedVote);
    }

    @Test
    public void equalsDifferentObjectClasses() {
        assertNotEquals(speedVote, "Margarita");
    }

    @Test
    public void equalsDifferentObjects() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(12, uuid, "faster");
        assertNotEquals(speedVote, speedVoteNew);
    }

}
