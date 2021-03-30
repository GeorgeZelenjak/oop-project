package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class UserQuestionPairIdTest {
    private static UserQuestionPairId pair;

    /**
     * Set up for the class.
     */
    @BeforeAll
    public static void setUp() {
        pair = new UserQuestionPairId(123456789, 987654321);
    }

    @Test
    public void constructorTest() {
        assertNotNull(pair);
    }

    @Test
    public void getUserIdTest() {
        assertEquals(123456789, pair.getUserId());
    }

    @Test
    public void setUserIdTest() {
        pair.setUserId(444);
        assertEquals(444, pair.getUserId());

        //change back for other tests
        pair.setUserId(123456789);
    }

    @Test
    public void getQuestionIdTest() {
        assertEquals(987654321, pair.getQuestionId());
    }

    @Test
    public void setQuestionIdTest() {
        pair.setQuestionId(777);
        assertEquals(777, pair.getQuestionId());

        //change back for other tests
        pair.setQuestionId(987654321);
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
        UserQuestionPairId pair1 = new UserQuestionPairId(123456789, 987654321);
        assertEquals(pair, pair1);
    }

    @Test
    public void equalsDifferentTest() {
        UserQuestionPairId pair1 = new UserQuestionPairId();
        assertNotEquals(pair, pair1);
    }

    @Test
    public void equalsDifferentUserIdTest() {
        UserQuestionPairId pair1 = new UserQuestionPairId(42, 987654321);
        assertNotEquals(pair, pair1);
    }

    @Test
    public void equalsDifferentQuestionIdTest() {
        UserQuestionPairId pair1 = new UserQuestionPairId(123456789, 69);
        assertNotEquals(pair, pair1);
    }

    @Test
    public void hashCodeTest() {
        int hash = Objects.hash(123456789, 987654321);
        assertEquals(hash, pair.hashCode());
    }

    @Test
    public void hashCodeEqualsTest() {
        UserQuestionPairId pair2 = new UserQuestionPairId(123456789, 987654321);
        assertEquals(pair2.hashCode(), pair.hashCode());
    }

    @Test
    public void hashCodeDifferentUidTest() {
        UserQuestionPairId pair2 = new UserQuestionPairId(123456799, 987654321);
        assertNotEquals(pair2.hashCode(), pair.hashCode());
    }

    @Test
    public void hashCodeDifferentQidTest() {
        UserQuestionPairId pair2 = new UserQuestionPairId(123456789, 987654322);
        assertNotEquals(pair2.hashCode(), pair.hashCode());
    }

}
