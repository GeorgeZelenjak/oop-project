package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import nl.tudelft.oopp.livechat.repositories.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

    /**
     * Setup to ensure test independence.
     */
    @BeforeEach
    public void setup() {
        userRepository.save(user1);
        lectureRepository.save(l1);
    }

    /**
     * Clean up to ensure test independence.
     */
    @AfterEach
    public void clean() {
        if (userRepository.findById(uid1).isPresent()) {
            userRepository.deleteById(uid1);
        }
        if (lectureRepository.findById(l1.getUuid()).isPresent()) {
            lectureRepository.deleteById(l1.getUuid());
        }
    }

    @Test
    void constructorTestAndNonStaticSetup() {
        assertNotNull(lectureSpeedService);
    }

    /**
     * Tests for setUserLectureSpeedVote.
     */
    @Test
    void setUserLectureSpeedVoteInvalidSpeedTest() {
        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "a lot faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid()));
    }

    @Test
    void setUserLectureSpeedVoteUserNotRegisteredTest() {
        lectureRepository.save(l2);

        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid2,
                l2.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid2,
                l2.getUuid()));
        lectureRepository.deleteById(l2.getUuid());
    }

    @Test
    void setUserLectureSpeedVoteNoLectureTest() {
        userRepository.save(user2);
        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid2,
                l2.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid2,
                l2.getUuid()));
        userRepository.deleteById(uid2);
    }

    @Test
    void setUserLectureSpeedVoteLectureClosedTest() {
        l2.close();
        lectureRepository.save(l2);
        userRepository.save(user2);

        assertEquals(-1, lectureSpeedService.setUserLectureSpeedVote(uid2,
                l2.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid2,
                l2.getUuid()));

        l2.reOpen();
        lectureRepository.save(l2);
        userRepository.deleteById(uid2);
    }

    @Test
    void setUserLectureSpeedVoteFasterTest() {
        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "faster"));
        UserLectureSpeedTable table = userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                                                l1.getUuid());
        assertNotNull(table);
        assertEquals("faster", table.getVoteOnLectureSpeed());

        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }

    @Test
    void setUserLectureSpeedVoteSlowerTest() {
        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "slower"));
        UserLectureSpeedTable table = userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid());
        assertNotNull(table);
        assertEquals("slower", table.getVoteOnLectureSpeed());

        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }

    @Test
    void setUserLectureSpeedVoteResetFasterTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "faster");

        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "faster"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid()));

        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }

    @Test
    void setUserLectureSpeedVoteResetSlowerTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "slower");

        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "slower"));
        assertNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid()));

        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }

    @Test
    void setUserLectureSpeedVoteToggleFasterTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "faster");

        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "slower"));
        UserLectureSpeedTable table = userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid());
        assertNotNull(table);
        assertEquals("slower", table.getVoteOnLectureSpeed());

        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }

    @Test
    void setUserLectureSpeedVoteToggleSlowerTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "slower");
        assertEquals(0, lectureSpeedService.setUserLectureSpeedVote(uid1,
                l1.getUuid(), "faster"));
        UserLectureSpeedTable table = userLectureSpeedRepository.findByUserIdAndLectureId(uid1,
                l1.getUuid());
        assertNotNull(table);
        assertEquals("faster", table.getVoteOnLectureSpeed());

        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }

    /**
     * Tests for resetLectureSpeed.
     */
    @Test
    void resetLectureSpeedLectureNullTest() {
        lectureRepository.save(l2);
        userRepository.save(user2);
        lectureSpeedService.setUserLectureSpeedVote(uid2, l2.getUuid(), "slower");
        lectureRepository.deleteById(l2.getUuid());

        assertEquals(-1, lectureSpeedService.resetLectureSpeed(l2.getUuid(), l2.getModkey()));
        assertNotNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid2, l2.getUuid()));

        userRepository.deleteById(uid2);
        userLectureSpeedRepository.deleteAllByLectureId(l2.getUuid());

    }

    @Test
    void resetLectureSpeedWrongModKeyTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "faster");

        assertEquals(-1, lectureSpeedService.resetLectureSpeed(l1.getUuid(), l2.getModkey()));
        assertNotNull(userLectureSpeedRepository.findByUserIdAndLectureId(uid1, l1.getUuid()));

        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }

    @Test
    void resetLectureSpeedSuccessfulTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "faster");

        assertEquals(0, lectureSpeedService.resetLectureSpeed(l1.getUuid(), l1.getModkey()));
        List<UserLectureSpeedTable> t = userLectureSpeedRepository.findAllByLectureId(l1.getUuid());
        assertTrue(t.isEmpty());

        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(l1.getUuid());
        assertEquals(0, lecture.getFasterCount());
        assertEquals(0, lecture.getSlowerCount());
    }

    @Test
    void getVotesLectureNullTest() {
        assertNull(lectureSpeedService.getVotes(l2.getUuid()));
    }

    @Test
    void getVotesSuccessfulBothTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "faster");
        user2.setLectureId(l1.getUuid());
        userRepository.save(user2);

        lectureSpeedService.setUserLectureSpeedVote(uid2, l1.getUuid(), "slower");

        List<Integer> expected = List.of(1, 1);
        assertEquals(expected, lectureSpeedService.getVotes(l1.getUuid()));

        userRepository.deleteById(uid2);
        user2.setLectureId(l2.getUuid());
        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());

    }

    @Test
    void getVotesSuccessfulOnlyOneTest() {
        lectureSpeedService.setUserLectureSpeedVote(uid1, l1.getUuid(), "faster");
        user2.setLectureId(l1.getUuid());
        userRepository.save(user2);

        lectureSpeedService.setUserLectureSpeedVote(uid2, l1.getUuid(), "faster");

        List<Integer> expected = List.of(2, 0);
        assertEquals(expected, lectureSpeedService.getVotes(l1.getUuid()));

        userRepository.deleteById(uid2);
        user2.setLectureId(l2.getUuid());
        userLectureSpeedRepository.deleteAllByLectureId(l1.getUuid());
    }
}
