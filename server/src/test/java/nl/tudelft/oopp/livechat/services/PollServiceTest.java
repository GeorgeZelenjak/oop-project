package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.exceptions.InvalidModkeyException;
import nl.tudelft.oopp.livechat.exceptions.LectureNotFoundException;
import nl.tudelft.oopp.livechat.repositories.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PollServiceTest {
    private static LectureEntity l1;
    private static LectureEntity l2;
    private static UUID modkey;

    private static UserEntity user1;
    private static final long uid1 = 235435452541204312L;

    private static UserEntity user2;
    private static final long uid2 = 5831676587688625524L;

    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    private static PollEntity poll1;
    private static PollEntity poll2;
    private static PollOptionEntity option1;
    private static PollOptionEntity option2;
    private static PollOptionEntity option3;
    private static PollOptionEntity option4;

    @Autowired
    private PollService pollService;

    @Autowired
    private PollOptionRepository pollOptionRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private UserPollVoteRepository userPollVoteRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureService lectureService;


    /**
     * Setup for the tests.
     */
    @BeforeAll
    public static void setUp() {
        l1 = new LectureEntity("Aliens", "Alien", time);
        modkey = l1.getModkey();
        l2 = new LectureEntity("Predators", "Predator", time);

        user1 = new UserEntity(uid1, "Alien1", new Timestamp(
                System.currentTimeMillis()), true,
                "192.168.1.1", l1.getUuid());

        user2 = new UserEntity(uid2, "Alien2", new Timestamp(
                System.currentTimeMillis()), true,
                "192.185.7.3", l2.getUuid());

        poll1 = new PollEntity(l1.getUuid(),"What animal will we eat for lunch?",
                time, 10, true);
        poll2 = new PollEntity(l2.getUuid(),"What animal will we eat for dinner?",
                time, 7, false);

        option1 = new PollOptionEntity(poll1.getId(), "Seagull", 2, false);
        option2 = new PollOptionEntity(poll1.getId(), "Pelican", 1, true);
        option3 = new PollOptionEntity(poll2.getId(), "Platypus", 4, true);
        option4 = new PollOptionEntity(poll2.getId(), "Beaver", 7, false);
    }

    /**
     * Setup before each test.
     */
    @BeforeEach
    public void setup() {
        lectureRepository.save(l1);
        userRepository.save(user1);

    }

    @AfterEach
    public void clean() {
        lectureRepository.deleteById(l1.getUuid());
        userRepository.deleteById(user1.getUid());
    }

    @Test
    public void constructorTest() {
        assertNotNull(pollService);
    }

    @Test
    public void createPollNoLectureTest() {
        userRepository.save(user2);
        assertThrows(LectureNotFoundException.class, () ->
                pollService.createPoll(l2.getUuid(), l2.getModkey(), "Hello?"));

        userRepository.deleteById(user2.getUid());
    }

    @Test
    public void createPollIncorrectModkeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                pollService.createPoll(l1.getUuid(), l2.getModkey(), "Hello again?"));
    }

    @Test
    public void createPollSuccessfulTest() throws Exception {
        PollEntity poll = pollService.createPoll(l1.getUuid(), l1.getModkey(), "Delicious?");
        assertNotNull(poll);
        assertEquals("Delicious?", poll.getQuestionText());
    }
}