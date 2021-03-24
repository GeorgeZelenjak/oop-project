package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.UserQuestionTable;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserQuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;

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

    private static LectureEntity lecture;
    private static LectureEntity lecture1;
    private static LectureEntity lecture2;
    private static final UUID incorrectModKey = UUID.randomUUID();
    private static final String localhost = "127.0.0.1";
    private static final String ip = "192.168.1.1";


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserQuestionRepository userQuestionRepository;

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

        user = new UserEntity(uid, "root", time, true, null, lecture.getUuid());
        user1 = new UserEntity(18, "tux", time, true, null, lecture1.getUuid());
        user2 = new UserEntity(26, "gnu", time, true, null, lecture2.getUuid());
        q = new QuestionEntity(lecture.getUuid(), "question 0", time, uid);
        q1 = new QuestionEntity(lecture1.getUuid(), "question 1", time, uid1);
        q2 = new QuestionEntity(lecture2.getUuid(), "question 2", time, uid2);
    }

    @BeforeEach
    public void setupRepository() {
        lectureRepository.save(lecture);
        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);
        questionRepository.save(q);
        questionRepository.save(q1);
        questionRepository.save(q2);
        userService.newUser(user1, ip);
        userService.newUser(user2, ip);
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
        int result = userService.newUser(user, localhost);
        assertEquals(0, result);
        assertNotNull(userRepository.getUserEntityByUid(uid));

        userRepository.deleteById(uid);
    }

    @Test
    public void newUserUserNullTest() {
        int result = userService.newUser(null, localhost);
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
        int result = userService.newUser(user, localhost);
        assertEquals(-1, result);

        //set back for other tests
        user.setUid(uid);
    }

    @Test
    public void newUserTooManyUsersTest() {
        userRepository.save(new UserEntity(3253563653434523L, "root1",
                time, true, "192.168.1.2", lecture.getUuid()));
        userRepository.save(new UserEntity(3523452452345512L, "root2",
                time, true, "192.168.1.2", lecture.getUuid()));
        userRepository.save(new UserEntity(1458455343645123L, "root3",
                time, true, "192.168.1.2", lecture.getUuid()));
        userRepository.save(new UserEntity(5234525352452545L, "root4",
                time, true, "192.168.1.2", lecture.getUuid()));
        userRepository.save(new UserEntity(6346625635746253L, "root5",
                time, true, "192.168.1.2", lecture.getUuid()));
        int result = userService.newUser(new UserEntity(9886625635746450L, "root5",
                time, true, "192.168.1.2", lecture.getUuid()), "192.168.1.2"); //this code is valid
        assertEquals(-1, result);
        assertNull(userRepository.findById(9886625635746450L).orElse(null));

        userRepository.deleteById(3253563653434523L);
        userRepository.deleteById(3523452452345512L);
        userRepository.deleteById(1458455343645123L);
        userRepository.deleteById(5234525352452545L);
        userRepository.deleteById(6346625635746253L);
    }

    @Test
    public void banByIdNoQuestionTest() {
        assertEquals(-1, userService.banById(444, 42, lecture1.getModkey(), 10));

        UserEntity u = userRepository.getUserEntityByUid(uid1);
        assertNotNull(u);
        assertTrue(u.isAllowed());
    }

    @Test
    public void banByIdNoUserTest() {
        assertEquals(-1, userService.banById(444, q.getId(), lecture.getModkey(), 10));
    }

    @Test
    public void banByIdNoLectureTest() {
        lectureRepository.deleteById(lecture1.getUuid());
        assertEquals(-2, userService.banById(444, q1.getId(), lecture1.getModkey(), 10));

        UserEntity u = userRepository.getUserEntityByUid(uid1);
        assertNotNull(u);
        assertTrue(u.isAllowed());

        lectureRepository.save(lecture1);
    }

    @Test
    public void banByIdLectureClosedTest() {
        lecture1.close();
        lectureRepository.save(lecture1);

        assertEquals(-3, userService.banById(444, q1.getId(), lecture1.getModkey(), 10));
        UserEntity u = userRepository.getUserEntityByUid(uid1);
        assertNotNull(u);
        assertTrue(u.isAllowed());

        lecture1.reOpen();
        lectureRepository.save(lecture1);
    }

    @Test
    public void banByIdIncorrectModkeyTest() {
        assertEquals(-4, userService.banById(444, q2.getId(), incorrectModKey, 10));

        UserEntity u = userRepository.getUserEntityByUid(uid2);
        assertNotNull(u);
        assertTrue(u.isAllowed());
    }

    @Test
    public void banByIdAlreadyBannedTest() {
        user2.setAllowed(false);
        userRepository.save(user2);

        assertEquals(-5, userService.banById(444, q2.getId(), lecture2.getModkey(), 10));

        UserEntity u = userRepository.getUserEntityByUid(uid2);
        assertNotNull(u);
        assertFalse(u.isAllowed());

        user2.setAllowed(true);
        userRepository.save(user2);
    }

    @Test
    public void banByIdSuccessfulTest() {
        int result = userService.newUser(user, localhost);
        assertEquals(0, result);
        result = userService.banById(34, q.getId(), lecture.getModkey(), 10);
        assertEquals(0, result);
        UserEntity temp = userRepository.getUserEntityByUid(uid);
        assertFalse(temp.isAllowed());

        userRepository.deleteById(uid);
    }

    @Test
    public void banByIdSuccessfulTimeoutTest() throws InterruptedException {
        int result = userService.banById(34, q2.getId(), lecture2.getModkey(), 3);
        assertEquals(0, result);
        UserEntity temp = userRepository.getUserEntityByUid(uid2);
        assertFalse(temp.isAllowed());

        Thread.sleep(4000);

        temp = userRepository.getUserEntityByUid(uid2);
        assertTrue(temp.isAllowed());

        userRepository.deleteById(uid2);
    }

    @Test
    public void banByIdSuccessfulBannerIdTest() {
        int result = userService.banById(34, q2.getId(), lecture2.getModkey(), 3);
        assertEquals(0, result);
        UserEntity temp = userRepository.getUserEntityByUid(uid2);
        assertEquals(34, temp.getBannerId());

        userRepository.deleteById(uid2);
    }

    @Test
    public void banByIdSuccessfulBannerIdResetTest() throws InterruptedException {
        int result = userService.banById(34, q2.getId(), lecture2.getModkey(), 3);
        assertEquals(0, result);
        UserEntity temp = userRepository.getUserEntityByUid(uid2);
        assertEquals(34, temp.getBannerId());

        Thread.sleep(4000);

        temp = userRepository.getUserEntityByUid(uid2);
        assertEquals(0, temp.getBannerId());

        userRepository.deleteById(uid2);
    }

    @Test
    public void banByIdSuccessfulRepositoryChangesBeforeTest() {
        //change the second user to be the owner of the first question
        q.setOwnerName(user2.getUserName());
        q.setOwnerId(user2.getUid());
        questionRepository.save(q);
        userQuestionRepository.save(new UserQuestionTable(uid2, q2.getId()));

        int result = userService.banById(34, q2.getId(), lecture2.getModkey(), 5);
        assertEquals(0, result);

        //check if the question for which the user was banned is deleted
        assertNull(questionRepository.findById(q2.getId()).orElse(null));

        //check if the question-user pairs (voting) for the question
        //  for which the user was banned are deleted
        assertTrue(userQuestionRepository.getAllByQuestionId(q2.getId()).isEmpty());

        //check that all the questions have their owner name modified and are not deleted
        List<QuestionEntity> qs = questionRepository.findAllByOwnerId(uid2);
        assertTrue(qs.size() > 0);
        assertTrue(qs.get(0).getOwnerName().contains(" (banned)"));

        //set everything back to make the tests independent
        userRepository.deleteById(uid2);
        questionRepository.deleteById(q.getId());
        q.setOwnerName(user.getUserName());
        q.setOwnerId(uid);
    }

    @Test
    public void banByIdSuccessfulRepositoryChangesAfterTest() throws InterruptedException {
        //change the second user to be the owner of the zeroth question
        q.setOwnerName(user2.getUserName());
        q.setOwnerId(user2.getUid());
        questionRepository.save(q);
        userQuestionRepository.save(new UserQuestionTable(uid2, q2.getId()));

        int result = userService.banById(34, q2.getId(), lecture2.getModkey(), 5);
        assertEquals(0, result);

        //wait for the ban to end
        Thread.sleep(6000);

        //check that all the questions of user2 have their owner name changed back
        List<QuestionEntity> qs = questionRepository.findAllByOwnerId(uid2);
        assertTrue(qs.size() > 0);
        assertFalse(qs.get(0).getOwnerName().contains(" (banned)"));

        //set everything back to make the tests independent
        userRepository.deleteById(uid2);
        questionRepository.deleteById(q.getId());
        q.setOwnerName(user.getUserName());
        q.setOwnerId(uid);
    }

    @Test
    public void banByIpNoQuestionTest() {
        questionRepository.deleteById(q1.getId());

        int result = userService.banByIp(34, q1.getId(), lecture1.getModkey(), 10);
        assertEquals(-1, result);
        List<UserEntity> banned = userRepository.findAllByIp(ip);
        banned.forEach((u) -> assertTrue(u.isAllowed()));

        questionRepository.save(q1);
    }

    @Test
    public void banByIpNoOwnerTest() {
        userRepository.deleteById(uid1);

        int result = userService.banByIp(34, q1.getId(), lecture1.getModkey(), 10);
        assertEquals(-1, result);
        List<UserEntity> banned = userRepository.findAllByIp(ip);
        banned.forEach((u) -> assertTrue(u.isAllowed()));

        userRepository.save(user1);
    }

    @Test
    public void banByIpClosedLecturesTest() {
        lecture1.close();
        lecture2.close();
        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);

        int result = userService.banByIp(34, q1.getId(), lecture1.getModkey(), 10);
        assertEquals(-2, result);
        List<UserEntity> banned = userRepository.findAllByIp(ip);
        banned.forEach((u) -> assertTrue(u.isAllowed()));

        lecture1.reOpen();
        lecture2.reOpen();
        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);
    }

    @Test
    public void banByIpWrongModKeyTest() {
        int result = userService.banByIp(34, q1.getId(), incorrectModKey, 10);
        assertEquals(-2, result);
        List<UserEntity> banned = userRepository.findAllByIp(ip);
        banned.forEach((u) -> assertTrue(u.isAllowed()));
    }

    @Test
    public void banByIpAlreadyBannedTest() {
        user1.setAllowed(false);
        user2.setAllowed(false);
        userRepository.save(user1);
        userRepository.save(user2);

        int result = userService.banByIp(34, q1.getId(), lecture1.getModkey(), 10);
        assertEquals(-3, result);
        List<UserEntity> banned = userRepository.findAllByIp("192.168.1.1");
        banned.forEach((u) -> assertFalse(u.isAllowed()));

        user1.setAllowed(true);
        user2.setAllowed(true);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void banByIpSuccessfulAndResetTest() throws InterruptedException {
        int result = userService.banByIp(34, q1.getId(), lecture1.getModkey(), 4);
        assertEquals(0, result);
        List<UserEntity> banned = userRepository.findAllByIp(ip);
        banned.forEach((u) -> {
            assertFalse(u.isAllowed());
            assertEquals(34, u.getBannerId());
        });

        Thread.sleep(5000);

        banned = userRepository.findAllByIp(ip);
        banned.forEach((u) -> {
            assertTrue(u.isAllowed());
            assertEquals(0, u.getBannerId());
        });
    }

    @Test
    public void banByIpSuccessfulRepositoryCheckBeforeTest() {
        //change the second user to be the owner of the zeroth question
        q.setOwnerName(user2.getUserName());
        q.setOwnerId(user2.getUid());
        questionRepository.save(q);
        userQuestionRepository.save(new UserQuestionTable(uid2, q2.getId()));
        userQuestionRepository.save(new UserQuestionTable(uid1, q2.getId()));

        int result = userService.banByIp(34, q2.getId(), lecture2.getModkey(), 4);
        assertEquals(0, result);

        //check if the question for which the user was banned is deleted
        assertNull(questionRepository.findById(q2.getId()).orElse(null));

        //check if the question-user pairs (voting) for the question
        //  for which the user was banned are deleted
        assertTrue(userQuestionRepository.getAllByQuestionId(q2.getId()).isEmpty());

        //check that all the questions of user2 have their owner name modified and are not deleted
        List<QuestionEntity> qs = questionRepository.findAllByOwnerId(uid2);
        assertTrue(qs.size() > 0);
        qs.forEach(q -> assertTrue(q.getOwnerName().contains(" (banned)")));

        //check that all the questions of user1 have their owner name modified and are not deleted
        // (since user1 and user2 have the same ip)
        qs = questionRepository.findAllByOwnerId(uid1);
        assertTrue(qs.size() > 0);
        qs.forEach(q -> assertTrue(q.getOwnerName().contains(" (banned)")));

        //set everything back to make the tests independent
        userRepository.deleteById(uid2);
        questionRepository.deleteById(q.getId());
        q.setOwnerName(user.getUserName());
        q.setOwnerId(uid);
    }

    @Test
    public void banByIpSuccessfulRepositoryCheckAfterTest() throws InterruptedException {
        //change the second user to be the owner of the zeroth question
        q.setOwnerName(user2.getUserName());
        q.setOwnerId(user2.getUid());
        questionRepository.save(q);
        userQuestionRepository.save(new UserQuestionTable(uid2, q2.getId()));
        userQuestionRepository.save(new UserQuestionTable(uid1, q2.getId()));

        int result = userService.banByIp(34, q2.getId(), lecture2.getModkey(), 3);
        assertEquals(0, result);

        //wait for the ban to end
        Thread.sleep(4000);

        //check that all the questions of user2 have their owner name changed back
        List<QuestionEntity> qs = questionRepository.findAllByOwnerId(uid2);
        assertTrue(qs.size() > 0);
        qs.forEach(q -> assertFalse(q.getOwnerName().contains(" (banned)")));

        //check that all the questions of user1 have their owner name changed back
        // (since user1 and user2 have the same ip)
        qs = questionRepository.findAllByOwnerId(uid1);
        assertTrue(qs.size() > 0);
        qs.forEach(q -> assertFalse(q.getOwnerName().contains(" (banned)")));

        //set everything back to make the tests independent
        userRepository.deleteById(uid2);
        questionRepository.deleteById(q.getId());
        q.setOwnerName(user.getUserName());
        q.setOwnerId(uid);
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
