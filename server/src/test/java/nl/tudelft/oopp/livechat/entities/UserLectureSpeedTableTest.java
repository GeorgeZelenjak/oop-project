package nl.tudelft.oopp.livechat.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import java.util.UUID;

public class UserLectureSpeedTableTest {

    private static UserLectureSpeedTable speedVote;
    private static UUID uuid;
    private static long uid;

    /**
     * Sets up everything.
     */
    @BeforeAll
    public static void setUp() {
        uuid = UUID.randomUUID();
        uid = 4156345676245155234L;
        speedVote = new UserLectureSpeedTable(uid, uuid, "faster");
    }

    @Test
    public void emptyConstructorTest() {
        UserLectureSpeedTable speedVote1 = new UserLectureSpeedTable();
        assertNotNull(speedVote1);
    }

    @Test
    public void constructorTest() {
        assertNotNull(speedVote);
    }

    @Test
    public void getUidTest() {
        assertEquals(uid, speedVote.getUid());
    }

    @Test
    public void setUidTest() {
        speedVote.setUid(123453463576234L);
        assertEquals(123453463576234L, speedVote.getUid());

        //set back for other tests
        speedVote.setUid(uid);
    }

    @Test
    public void getUuidTest() {
        assertEquals(uuid, speedVote.getUuid());
    }

    @Test
    public void setUuidTest() {
        UUID newUuid = UUID.randomUUID();
        speedVote.setUuid(newUuid);
        assertEquals(newUuid, speedVote.getUuid());

        //set back for other tests
        speedVote.setUuid(uuid);
    }

    @Test
    public void getVoteOnLectureSpeedTest() {
        assertEquals("faster", speedVote.getVoteOnLectureSpeed());
    }

    @Test
    public void setVoteOnLectureSpeedTest() {
        speedVote.setVoteOnLectureSpeed("slower");
        assertEquals("slower", speedVote.getVoteOnLectureSpeed());

        //set back for other tests
        speedVote.setVoteOnLectureSpeed("faster");
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(speedVote, null);
    }

    @Test
    public void equalsSameTest() {
        assertEquals(speedVote, speedVote);
    }

    @Test
    public void equalsEqualTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(uid, uuid, "slower");
        assertEquals(speedVoteNew, speedVote);
    }

    @Test
    public void equalsDifferentUidTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(212343512341453L,
                uuid, "faster");
        assertNotEquals(speedVote, speedVoteNew);
    }

    @Test
    public void equalsDifferentUuidTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(uid,
                UUID.randomUUID(), "faster");
        assertNotEquals(speedVote, speedVoteNew);
    }

    @Test
    public void hashCodeTest() {
        int hash = Objects.hash(uid, uuid);
        assertEquals(hash, speedVote.hashCode());
    }

    @Test
    public void hashCodeEqualTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(uid,
                uuid, "faster");
        assertEquals(speedVoteNew.hashCode(), speedVote.hashCode());
    }

    @Test
    public void hashCodeDifferentUidTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(uid + 1,
                uuid, "slower");
        assertNotEquals(speedVoteNew.hashCode(), speedVote.hashCode());
    }

    @Test
    public void hashCodeDifferentUuidTest() {
        UserLectureSpeedTable speedVoteNew = new UserLectureSpeedTable(uid,
                UUID.randomUUID(), "faster");
        assertNotEquals(speedVoteNew.hashCode(), speedVote.hashCode());
    }
}
