package nl.tudelft.oopp.livechat.datatest;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.oopp.livechat.data.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class UserTest {

    @BeforeAll
    public static void setUp() {
        User.setUid();
        User.setUserName("Bobby from the lobby");
    }

    @Test
    public void getUidTest() {
        assertNotEquals(0, User.getUid());
    }

    @Test
    public void getUsernameTest() {
        assertEquals("Bobby from the lobby", User.getUserName());
    }
}
