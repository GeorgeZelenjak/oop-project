package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;

import org.h2.engine.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


/**
 * Class for Question service tests.
 */
@SpringBootTest
class UserServiceTest {
    private static UserEntity user;
    private static UserEntity user1;
    private static UserEntity user2;
    private static QuestionEntity q;
    private static QuestionEntity q1;
    private static QuestionEntity q2;

    private static final long uid = createUid();
    private static final long uid1 = 18;
    private static final long uid2 = 26;
    private static final Timestamp time = new Timestamp(
            System.currentTimeMillis() / 1000 * 1000);
    private static UUID lid;
    private static UUID lid1;
    private static UUID lid2;
    private static LectureEntity lecture;
    private static LectureEntity lecture1;
    private static LectureEntity lecture2;
    private static UUID modkey;
    private static UUID modkey1;
    private static UUID modkey2;
    private static final UUID incorrectModKey = UUID.randomUUID();


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private QuestionRepository questionRepository;

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
     * @param n the number
     * @return the luhn digit
     */
    private static long getLuhnDigit(long n) {
        String number = Long.toString(n);
        long temp = 0;
        for (int i = number.length() - 1;i >= 0;i--) {
            int digit;
            if ((number.length() - i) % 2 == 1) {
                digit = Character.getNumericValue(number.charAt(i)) * 2;
                if (digit > 9) {
                    digit %= 9;
                    if (digit == 0) digit = 9;
                }
            } else {
                digit = Character.getNumericValue(number.charAt(i));
            }
            temp += digit;
        }
        return (10 - (temp % 10)) % 10;
    }

    @BeforeAll
    public static void setUp() {
        lecture = new LectureEntity();
        lecture1 = new LectureEntity();
        lecture2 = new LectureEntity();

        lid = lecture.getUuid();
        lid1 = lecture1.getUuid();
        lid2 = lecture2.getUuid();

        user = new UserEntity(uid, "root", time, true, null, lid);
        user1 = new UserEntity(18, "tux", time, true, null, lid1);
        user2 = new UserEntity(26, "gnu", time, true, null, lid2);
        q = new QuestionEntity(lid, "question 0", time, uid);
        q1 = new QuestionEntity(lid, "question 1", time, uid1);
        q2 = new QuestionEntity(lid, "question 2", time, uid2);

        modkey = lecture.getModkey();
        modkey1 = lecture1.getModkey();
        modkey2 = lecture2.getModkey();
    }

    @BeforeEach
    public void setupRepository() {
        lectureRepository.save(lecture);
        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);
        questionRepository.save(q);
        questionRepository.save(q1);
        questionRepository.save(q2);
        userService.newUser(user1, "192.168.1.1");
        userService.newUser(user2, "192.168.1.1");
    }

    @AfterEach
    public void clean() {
        lectureRepository.delete(lecture);
        lectureRepository.delete(lecture1);
        lectureRepository.delete(lecture2);
        userRepository.delete(user1);
        userRepository.delete(user2);
        questionRepository.delete(q);
        questionRepository.delete(q1);
        questionRepository.delete(q2);

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
        user.setUid(420);
        int result = userService.newUser(user, "127.0.0.1");
        assertEquals(-1, result);

        //set back for other tests
        user.setUid(uid);
    }

    @Test
    public void banByIdSuccessfulTest() {
        int result = userService.newUser(user, "127.0.0.1");
        assertEquals(0, result);
        result = userService.banById(34, q.getId(), modkey, 10);
        assertEquals(0, result);
        UserEntity temp = userRepository.getUserEntityByUid(uid);
        assertFalse(temp.isAllowed());
    }

    @Test
    public void banByIpSuccessfulTest() {
        int result = userService.banByIp(34, q1.getId(), modkey1, 10);
        assertEquals(0, result);
        List<UserEntity> banned = userRepository.findAllByIp("192.168.1.1");
        banned.forEach((u) -> assertFalse(u.isAllowed()));
    }

    @Test
    public void banByIpUnsuccessfulTest() {
        userService.newUser(user, "127.0.0.1");
        int result = userService.banByIp(34, q.getId(), incorrectModKey, 10);
        assertEquals(-2, result);
        List<UserEntity> banned = userRepository.findAllByIp("127.0.0.1");
        banned.forEach((u) -> assertTrue(u.isAllowed()));
    }

    @Test
    public void luhnCheckValidTest() {
        assertTrue(UserService.luhnCheck(1008L));
    }

    @Test
    public void luhnCheckInvalidTest() {
        assertFalse(UserService.luhnCheck(100L));
    }

    @Test
    public void testLuhnDigitIsOneDigit() {
        assertEquals(getLuhnDigit(100000000000L), getLuhnDigit(100000000000L) % 10);
    }

    @Test
    public void luhnDigitIs9Times2Test() {
        long luhnDigit = getLuhnDigit(7989);
        assertEquals(7L, luhnDigit);
        assertTrue(UserService.luhnCheck(79897L));
    }

}
