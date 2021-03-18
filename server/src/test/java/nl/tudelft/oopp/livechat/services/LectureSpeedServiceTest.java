package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import nl.tudelft.oopp.livechat.repositories.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LectureSpeedServiceTest {
    private static LectureEntity l1;
    private static LectureEntity l2;

    private static UserEntity user1;
    private static final long uid1 = 867659508412049312L;
    private static UserEntity user2;
    private static final long uid2 = 543855539877625524L;

    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    @Autowired
    LectureSpeedService lectureSpeedService;

    @Autowired
    UserLectureSpeedRepository userLectureSpeedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LectureRepository lectureRepository;

    /**
     * Setup for the tests.
     */
    @BeforeAll
    public static void setUp() {
        l1 = new LectureEntity("sudo apt update", "root", time);
        l2 = new LectureEntity("sudo apt upgrade", "root", time);

        user1 = new UserEntity(uid1, "Donna", new Timestamp(
                System.currentTimeMillis()), true,
                "192.168.1.1", l1.getUuid());

        user2 = new UserEntity(uid2, "Marty", new Timestamp(
                System.currentTimeMillis()), false,
                "192.185.7.3", l2.getUuid());
    }

    @Test
    @Order(1)
    void constructorTestAndNonStaticSetup() {
        assertNotNull(lectureSpeedService);

        userRepository.save(user1);
        lectureRepository.save(l1);
    }

    /**
     * Tests for setUserLectureSpeedVote.
     */
    @Test
    @Order(2)
    void setUserLectureSpeedVoteInvalidSpeedTest() {
        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "a lot faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid()));
    }

    @Test
    @Order(3)
    void setUserLectureSpeedVoteUserNotRegisteredTest() {
        userRepository.deleteById(uid1);
        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid()));

        userRepository.save(user1);
    }

    @Test
    @Order(4)
    void setUserLectureSpeedVoteNoLectureTest() {
        userRepository.save(user2);
        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid2,
                l2.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid2,
                l2.getUuid()));
    }

    @Test
    @Order(5)
    void setUserLectureSpeedVoteLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid2,
                l2.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid2,
                l2.getUuid()));

        l2.reOpen();
        lectureRepository.save(l2);
    }

    @Test
    @Order(6)
    void setUserLectureSpeedVoteFasterTest() {
        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "faster"));
        UserLectureSpeedTable table = userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                                                l1.getUuid());
        assertNotNull(table);
        assertEquals("faster", table.getVoteOnLectureSpeed());
    }

    @Test
    @Order(7)
    void setUserLectureSpeedVoteSlowerTest() {
        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid2,
                l2.getUuid(), "slower"));
        UserLectureSpeedTable table = userLectureSpeedRepository.findByUserIdAndLectureId(uid2,
                l2.getUuid());
        assertNotNull(table);
        assertEquals("slower", table.getVoteOnLectureSpeed());
    }

    @Test
    @Order(8)
    void setUserLectureSpeedVoteResetFasterTest() {
        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid()));
    }

    @Test
    @Order(9)
    void setUserLectureSpeedVoteResetSlowerTest() {
        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid2,
                l2.getUuid(), "slower"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid2,
                l2.getUuid()));
    }
}
