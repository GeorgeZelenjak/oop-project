package nl.tudelft.oopp.livechat.datatest;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.oopp.livechat.data.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;


public class UserTest {

    /**
     * Initialization.
     */
    @BeforeAll
    public static void setUp() {
        User.setUid();
        User.setUserName("Bobby from the lobby");

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(123456789L);
        questionIds.add(987654321L);

        User.setAskedQuestionsIds(questionIds);
    }

    @Test
    void getUidTest() {
        assertNotEquals(0, User.getUid());
    }

    @Test
    public void setUidTest() {
        long oldUid = User.getUid();
        User.setUid();
        assertEquals(oldUid, User.getUid());
    }

    @Test
    public void getUsernameTest() {
        assertEquals("Bobby from the lobby", User.getUserName());
    }

    @Test
    public void setUsernameTest() {
        User.setUserName("Vladimir Putin");
        assertEquals("Vladimir Putin", User.getUserName());
    }

    @Test
    public void getAskedQuestionsIdsTest() {
        assertNotNull(User.getAskedQuestionIds());
    }

    @Test
    public void setAskedQuestionsIdsTest() {
        Set<Long> ids = new HashSet<>();
        ids.add(42L);
        ids.add(69L);
        User.setAskedQuestionsIds(ids);

        assertEquals(ids, User.getAskedQuestionIds());
    }

    @Test
    public void addQuestionIdTest() {
        int oldSize = User.getAskedQuestionIds().size();
        User.addQuestionId(444555666777L);

        assertEquals(oldSize + 1, User.getAskedQuestionIds().size());
        assertTrue(User.getAskedQuestionIds().contains(444555666777L));
    }

    @Test
    public void deleteQuestionIdSuccessfulTest() {
        User.addQuestionId(111122223333L);
        int oldSize = User.getAskedQuestionIds().size();
        User.deleteQuestionId(111122223333L);

        assertEquals(oldSize - 1, User.getAskedQuestionIds().size());
        assertFalse(User.getAskedQuestionIds().contains(111122223333L));
    }

    @Test
    public void deleteQuestionIdUnsuccessfulTest() {
        int oldSize = User.getAskedQuestionIds().size();
        User.deleteQuestionId(666666666666L);

        assertEquals(oldSize, User.getAskedQuestionIds().size());
    }

    @Test
    public void luhnDigitIs9Times2Test() {
        long luhnDigit = User.getLuhnDigit(7989);
        assertEquals(7L, luhnDigit);
    }
}
