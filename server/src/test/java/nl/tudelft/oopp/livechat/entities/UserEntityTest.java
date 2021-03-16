package nl.tudelft.oopp.livechat.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;

public class UserEntityTest {
    private static UserEntity user;
    private static long uid;
    private static final Timestamp lastTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

    /**
     * A method to generate user id based on the mac address.
     * @return the generated user id
     */
    private static long createUid() {
        byte[] hardwareAddress;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            hardwareAddress = ni.getHardwareAddress();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return -1;
        }
        long uidTemp = 0;
        for (int i = 0;i < hardwareAddress.length;i++) {
            long unsigned = (long) hardwareAddress[i] & 0xFF;
            uidTemp += unsigned << (8 * i);
        }
        return uidTemp;
    }

    @BeforeAll
    public static void setUp() {
        uid = createUid();
        user = new UserEntity(uid, "root", lastTime, true);
    }

    @Test
    void getUidTest() {
        assertEquals(uid, user.getUid());
    }

    @Test
    void setUidTest() {
        user.setUid(42);
        assertEquals(42, user.getUid());

        //set it back for other tests
        user.setUid(uid);
    }

    @Test
    void getUsernameTest() {
        assertEquals("root", user.getUserName());
    }

    @Test
    void setUserNameTest() {
        user.setUserName("sudo");
        assertEquals("sudo", user.getUserName());

        //set it back for other tests
        user.setUserName("root");
    }


    @Test
    void getLastQuestionTest() {
        assertEquals(lastTime, user.getLastQuestion());
    }

    @Test
    void setLastQuestionTest() {
        Timestamp newTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        user.setLastQuestion(newTime);

        assertEquals(newTime, user.getLastQuestion());

        //set it back for other tests
        user.setLastQuestion(lastTime);
    }

    @Test
    void isAllowedTest() {
        assertTrue(user.isAllowed());
    }


    @Test
    void setAllowedTest() {
        boolean oldState = user.isAllowed();

        user.setAllowed(!oldState);
        assertEquals(!oldState, user.isAllowed());

        user.setAllowed(oldState);
    }

    @Test
    void hashCodeTest() {
        int hash = (int) (uid >> 32) + (int) uid;
        assertEquals(hash, user.hashCode());
    }

    @Test
    void toStringTest() {
        String userStr = "username: 'root', user id: " + uid;
        assertEquals(userStr, user.toString());
    }

    @Test
    void equalsNullTest() {
        assertNotEquals(user, null);
    }

    @Test
    void equalsSameTest() {
        assertEquals(user, user);
    }

    @Test
    void equalsEqualTest() {
        UserEntity user1 = new UserEntity(uid, "sudo", lastTime, true);
        assertEquals(user, user1);
    }

    @Test
    void equalsDifferentTest() {
        UserEntity user1 = new UserEntity(42, "root", lastTime, true);
        assertNotEquals(user, user1);
    }
}
