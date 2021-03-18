package nl.tudelft.oopp.livechat.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserLectureSpeedPairIdTest {
    private static UserLectureSpeedPairId pair;
    private static UUID uuid;
    private static long uid;

    /**
     * Sets up everything.
     */
    @BeforeAll
    public static void setUp() {
        uuid = UUID.randomUUID();
        uid = 8876945752879356254L;
        pair = new UserLectureSpeedPairId(uuid, uid);
    }

    @Test
    public void emptyConstructorTest() {
        UserLectureSpeedPairId pair1 = new UserLectureSpeedPairId();
        assertNotNull(pair1);
    }

    @Test
    public void constructorTest() {
        assertNotNull(pair);
    }

    @Test
    public void getUserIdTest() {
        assertEquals(uid, pair.getUserId());
    }

    @Test
    public void setUserIdTest() {
        pair.setUserId(44666453463576234L);
        assertEquals(44666453463576234L, pair.getUserId());

        //set back for other tests
        pair.setUserId(uid);
    }

    @Test
    public void getLectureIdTest() {
        assertEquals(uuid, pair.getLectureId());
    }

    @Test
    public void setLectureIdTest() {
        UUID newUuid = UUID.randomUUID();
        pair.setLectureId(newUuid);
        assertEquals(newUuid, pair.getLectureId());

        //set back for other tests
        pair.setLectureId(uuid);
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(pair, null);
    }

    @Test
    public void equalsSameTest() {
        assertEquals(pair, pair);
    }

    @Test
    public void equalsEqualTest() {
        UserLectureSpeedPairId pair1 = new UserLectureSpeedPairId(uuid, uid);
        assertEquals(pair, pair1);
    }

    @Test
    public void equalsDifferentUidTest() {
        UserLectureSpeedPairId pair1 = new UserLectureSpeedPairId(uuid, uid - 1);
        assertNotEquals(pair, pair1);
    }

    @Test
    public void equalsDifferentUuidTest() {
        UserLectureSpeedPairId pair1 = new UserLectureSpeedPairId(UUID.randomUUID(), uid);
        assertNotEquals(pair, pair1);
    }

    @Test
    public void hashCodeTest() {
        int hash = Objects.hash(uuid, uid);
        assertEquals(hash, pair.hashCode());
    }

    @Test
    public void hashCodeEqualTest() {
        UserLectureSpeedPairId pair1 = new UserLectureSpeedPairId(uuid, uid);
        assertEquals(pair.hashCode(), pair1.hashCode());
    }

    @Test
    public void hashCodeDifferentUidTest() {
        UserLectureSpeedPairId pair1 = new UserLectureSpeedPairId(uuid, uid + 1);
        assertNotEquals(pair.hashCode(), pair1.hashCode());
    }

    @Test
    public void hashCodeDifferentUuidTest() {
        UserLectureSpeedPairId pair1 = new UserLectureSpeedPairId(UUID.randomUUID(), uid);
        assertNotEquals(pair.hashCode(), pair1.hashCode());
    }
}
