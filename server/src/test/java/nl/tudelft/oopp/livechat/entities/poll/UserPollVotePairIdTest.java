package nl.tudelft.oopp.livechat.entities.poll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UserPollVotePairIdTest {
    private static UserPollVotePairId userPollVotePairId;

    @BeforeAll
    static void setUp() {
        userPollVotePairId = new UserPollVotePairId(4242, 6969);
    }

    @Test
    void emptyConstructorTest() {
        UserPollVotePairId up = new UserPollVotePairId();
        assertNotNull(up);
    }

    @Test
    void fullConstructorTest() {
        assertNotNull(userPollVotePairId);
    }

    @Test
    void getUserIdTest() {
        assertEquals(4242, userPollVotePairId.getUserId());
    }

    @Test
    void setUserIdTest() {
        userPollVotePairId.setUserId(42);
        assertEquals(42, userPollVotePairId.getUserId());

        userPollVotePairId.setUserId(4242);
    }

    @Test
    void getOptionIdTest() {
        assertEquals(6969, userPollVotePairId.getOptionId());
    }

    @Test
    void setOptionIdTest() {
        userPollVotePairId.setOptionId(69);
        assertEquals(69, userPollVotePairId.getOptionId());

        userPollVotePairId.setOptionId(6969);
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(userPollVotePairId, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(userPollVotePairId, userPollVotePairId);
    }

    @Test
    void equalsEqualTest() {
        UserPollVotePairId up = new UserPollVotePairId(4242, 6969);
        assertEquals(userPollVotePairId, up);
    }

    @Test
    void equalsDifferentUserIdTest() {
        UserPollVotePairId up = new UserPollVotePairId(4241, 6969);
        assertNotEquals(userPollVotePairId, up);
    }

    @Test
    void equalsDifferentOptionIdTest() {
        UserPollVotePairId up = new UserPollVotePairId(4242, 6999);
        assertNotEquals(userPollVotePairId, up);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(userPollVotePairId.getUserId(), userPollVotePairId.getOptionId());
        assertEquals(hash, userPollVotePairId.hashCode());
    }

    @Test
    void hashCodeEqualTest() {
        UserPollVotePairId up = new UserPollVotePairId(4242, 6969);
        assertEquals(up.hashCode(), userPollVotePairId.hashCode());
    }

    @Test
    void hashCodeDifferentUserIdTest() {
        UserPollVotePairId up = new UserPollVotePairId(4243, 6969);
        assertNotEquals(up.hashCode(), userPollVotePairId.hashCode());
    }

    @Test
    void hashCodeDifferentOptionIdTest() {
        UserPollVotePairId up = new UserPollVotePairId(4242, 6968);
        assertNotEquals(up.hashCode(), userPollVotePairId.hashCode());
    }
}