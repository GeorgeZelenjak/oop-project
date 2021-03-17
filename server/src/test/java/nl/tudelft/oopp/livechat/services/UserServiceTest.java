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
    private UserService userService;

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
        return uidTemp * 10 + getLuhnDigit(uidTemp);
    }

    /**
     * Gets luhn digit to make luhn checksum valid.
     *
     * @param n the number
     * @return the luhn digit
     */
    public static long getLuhnDigit(long n) {
        String number = Long.toString(n);
        long temp = 0;
        for (int i = 0;i < number.length();i++) {
            int digit;
            if (i % 2 == 1) {
                digit = Character.getNumericValue(number.charAt(i)) * 2;
                digit %= 9;
                if (digit == 0) digit = 9;
            } else {
                digit = Character.getNumericValue(number.charAt(i));
            }
            temp += digit;
        }
        return (10 - (temp % 10)) % 10;
    }

    @BeforeAll
    public static void setUp() {
        user = new UserEntity(uid, "root", time, true, null, lid);
    }

    @Test
    public void constructorTest() {
        assertNotNull(userService);
        assertNotNull(userRepository);
    }

    @Test
    public void newUserSuccessfulTest() {
        int result = userService.newUser(user, "127.0.0.1");
        assertEquals(0, result);
        assertNotNull(userRepository.getUserEntityByUid(uid));
    }

    @Test
    public void newUserUserNullTest() {
        int result = userService.newUser(null, "127.0.0.1");
        assertEquals(-1, result);
    }

    @Test
    public void newUserIPNullTest() {
        int result = userService.newUser(user, null);
        assertEquals(-1, result);
    }

    @Test
    public void newUserInvalidUidTest() {
        user.setUid(42);
        int result = userService.newUser(user, "127.0.0.1");
        assertEquals(-1, result);

        //set back for other tests
        user.setUid(uid);
    }

}
