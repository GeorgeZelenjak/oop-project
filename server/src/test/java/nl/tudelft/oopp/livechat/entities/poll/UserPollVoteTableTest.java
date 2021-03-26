package nl.tudelft.oopp.livechat.entities.poll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UserPollVoteTableTest {
    private static UserPollVoteTable userPollVoteTable;

    @BeforeAll
    static void setUp() {
        userPollVoteTable = new UserPollVoteTable(5432, 8080);
    }

    @Test
    void emptyConstructorTest() {
        UserPollVoteTable up = new UserPollVoteTable();
        assertNotNull(up);
    }

    @Test
    void fullConstructorTest() {
        assertNotNull(userPollVoteTable);
    }

    @Test
    void getUserIdTest() {
        assertEquals(5432, userPollVoteTable.getUserId());
    }

    @Test
    void setUserIdTest() {
        userPollVoteTable.setUserId(54);
        assertEquals(54, userPollVoteTable.getUserId());

        userPollVoteTable.setUserId(5432);
    }

    @Test
    void getOptionIdTest() {
        assertEquals(8080, userPollVoteTable.getOptionId());
    }

    @Test
    void setOptionIdTest() {
        userPollVoteTable.setOptionId(80);
        assertEquals(80, userPollVoteTable.getOptionId());

        userPollVoteTable.setOptionId(8080);
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(userPollVoteTable, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(userPollVoteTable, userPollVoteTable);
    }

    @Test
    void equalsEqualTest() {
        UserPollVoteTable up = new UserPollVoteTable(5432, 8080);
        assertEquals(userPollVoteTable, up);
    }

    @Test
    void equalsDifferentUserIdTest() {
        UserPollVoteTable up = new UserPollVoteTable(5431, 8080);
        assertNotEquals(userPollVoteTable, up);
    }

    @Test
    void equalsDifferentOptionIdTest() {
        UserPollVoteTable up = new UserPollVoteTable(5432, 8081);
        assertNotEquals(userPollVoteTable, up);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(userPollVoteTable.getUserId(), userPollVoteTable.getOptionId());
        assertEquals(hash, userPollVoteTable.hashCode());
    }

    @Test
    void hashCodeEqualTest() {
        UserPollVoteTable up = new UserPollVoteTable(5432, 8080);
        assertEquals(up.hashCode(), userPollVoteTable.hashCode());
    }

    @Test
    void hashCodeDifferentUserIdTest() {
        UserPollVoteTable up = new UserPollVoteTable(5430, 8080);
        assertNotEquals(up.hashCode(), userPollVoteTable.hashCode());
    }

    @Test
    void hashCodeDifferentOptionIdTest() {
        UserPollVoteTable up = new UserPollVoteTable(5432, 8082);
        assertNotEquals(up.hashCode(), userPollVoteTable.hashCode());
    }
}