package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.repositories.UserRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.UUID;


/**
 * Class for Question service tests.
 */
@SpringBootTest
class UserServiceTest {
    private static UserEntity user;
    private static final long uid = createUid();
    private static final Timestamp time = new Timestamp(
            System.currentTimeMillis() / 1000 * 1000);
    private static final UUID lid = UUID.randomUUID();

    @Autowired
    UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * A method to generate user id based.
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
        user = new UserEntity(uid, "root", time, true, "192.168.1.1", lid);
    }

    @Test
    public void constructorTest() {
        assertNotNull(userService);
        assertNotNull(userRepository);
    }

    @Test
    public void newUserSuccessful() {
        int result = userService.newUser(user, "127.0.0.1");
        assertEquals(0, result);
        assertNotNull(userRepository.getUserEntityByUid(uid));
    }

    @Test
    public void newUserUserNull() {
        int result = userService.newUser(null, "127.0.0.1");
        assertEquals(-1, result);
    }

    @Test
    public void newUserIPNull() {
        int result = userService.newUser(user, null);
        assertEquals(-1, result);
    }

    @Test
    public void newUserInvalidUid() {
        user.setUid(42);
        int result = userService.newUser(user, "127.0.0.1");
        assertEquals(-1, result);

        //set back for other tests
        user.setUid(uid);
    }

}
