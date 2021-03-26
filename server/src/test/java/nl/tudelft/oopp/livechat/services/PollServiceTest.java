package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.repositories.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PollServiceTest {
    private static LectureEntity l1;
    private static LectureEntity l2;

    private static UserEntity user1;
    private static final long uid1 = 235435452541204312L;

    private static UserEntity user2;
    private static final long uid2 = 5831676587688625524L;

    private static final Timestamp time = new Timestamp(System.currentTimeMillis());

    private static PollEntity poll1;
    private static PollEntity poll2;
    private static PollOptionEntity option1;
    private static PollOptionEntity option2;

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
    }

    /**
     * Setup before each test (adds some entities to the repository).
     */
    @BeforeEach
    public void setup() {
        lectureRepository.save(l1);
        userRepository.save(user1);

        pollRepository.save(poll1);
        pollOptionRepository.save(option1);
        pollOptionRepository.save(option2);
    }

    /**
     * Removes entities from the repositories.
     */
    @AfterEach
    public void clean() {
        lectureRepository.deleteById(l1.getUuid());
        userRepository.deleteById(user1.getUid());

        pollRepository.deleteById(poll1.getId());
        pollOptionRepository.deleteById(option1.getId());
        pollOptionRepository.deleteById(option2.getId());
    }

    @Test
    public void constructorTest() {
        assertNotNull(pollService);
    }

    /**
     * Tests for create poll.
     */

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

        pollRepository.deleteById(poll.getId());
    }

    /**
     * Tests for toggle poll.
     */

    @Test
    public void togglePollNoLectureTest() {
        lectureRepository.deleteById(l1.getUuid());

        assertThrows(LectureNotFoundException.class, () ->
                pollService.togglePoll(poll1.getId(), l1.getModkey()));

        lectureRepository.save(l1);
    }

    @Test
    public void togglePollPollNotFoundTest() {
        lectureRepository.save(l2);

        assertThrows(PollNotFoundException.class, () ->
                pollService.togglePoll(poll2.getId(), l2.getModkey()));

        lectureRepository.deleteById(l2.getUuid());
    }

    @Test
    public void togglePollIncorrectModkeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                pollService.togglePoll(poll1.getId(), l2.getModkey()));
    }

    @Test
    public void togglePollSuccessfulTest() throws Exception {
        boolean oldState = pollRepository.findById(poll1.getId()).isOpen();

        assertEquals(0, pollService.togglePoll(poll1.getId(), l1.getModkey()));
        PollEntity poll = pollRepository.findById(poll1.getId());
        assertEquals(!oldState, poll.isOpen());

        assertEquals(0, pollService.togglePoll(poll1.getId(), l1.getModkey()));
        poll = pollRepository.findById(poll1.getId());
        assertEquals(oldState, poll.isOpen());
    }

    /**
     * Tests for adding options.
     */

    @Test
    public void addOptionNoLectureTest() {
        userRepository.save(user2);
        pollRepository.save(poll2);

        assertThrows(LectureNotFoundException.class, () ->
                pollService.addOption(poll2.getId(), l2.getModkey(), "Platypus", false));
        assertEquals(0, pollOptionRepository.findAllByPollId(poll2.getId()).size());

        userRepository.deleteById(user2.getUid());
        pollRepository.deleteById(poll2.getId());
    }

    @Test
    public void addOptionPollNotFoundTest() {
        lectureRepository.save(l2);

        assertThrows(PollNotFoundException.class, () ->
                pollService.addOption(poll2.getId(), l2.getModkey(), "Beaver", true));
        assertEquals(0, pollOptionRepository.findAllByPollId(poll2.getId()).size());

        lectureRepository.deleteById(l2.getUuid());
    }

    @Test
    public void addOptionIncorrectModkeyTest() {
        int oldSize = pollOptionRepository.findAllByPollId(poll1.getId()).size();
        assertThrows(InvalidModkeyException.class, () ->
                pollService.addOption(poll1.getId(), l2.getModkey(), "Elephant", true));
        assertEquals(oldSize, pollOptionRepository.findAllByPollId(poll1.getId()).size());
    }

    @Test
    public void addOptionSuccessfulTest() throws Exception {
        PollOptionEntity o1 = pollService.addOption(poll1.getId(),
                l1.getModkey(), "Elephant", true);
        assertNotNull(o1);
        assertEquals("Elephant", o1.getOptionText());
        assertEquals(poll1.getId(), o1.getPollId());

        PollOptionEntity o2 = pollService.addOption(poll1.getId(),
                l1.getModkey(), "Hippo", false);
        assertNotNull(o2);
        assertEquals("Hippo", o2.getOptionText());
        assertEquals(poll1.getId(), o2.getPollId());

        pollOptionRepository.deleteById(o1.getId());
        pollOptionRepository.deleteById(o2.getId());
    }

    /**
     * Tests for vote on poll.
     */

    @Test
    public void voteOnPollUserNotRegisteredTest() {
        userRepository.deleteById(user1.getUid());
        long oldVotes = pollOptionRepository.findById(option1.getId()).getVotes();

        assertThrows(UserNotRegisteredException.class, () ->
                pollService.voteOnPoll(user1.getUid(), option1.getId()));
        assertEquals(oldVotes, pollOptionRepository.findById(option1.getId()).getVotes());

        userRepository.save(user1);
    }

    @Test
    public void voteOnPollPollOptionNotFoundTest() {
        pollOptionRepository.deleteById(option1.getId());

        assertThrows(PollOptionNotFoundException.class, () ->
                pollService.voteOnPoll(user1.getUid(), option1.getId()));

        pollOptionRepository.save(option1);
    }

    @Test
    public void voteOnPollPollNotFoundTest() {
        pollRepository.deleteById(poll1.getId());

        assertThrows(PollNotFoundException.class, () ->
                pollService.voteOnPoll(user1.getUid(), option1.getId()));

        pollRepository.save(poll1);
    }

    @Test
    public void voteOnPollPollNotOpenTest() {
        poll1.setOpen(false);
        pollRepository.save(poll1);
        long oldVotes = pollOptionRepository.findById(option1.getId()).getVotes();

        assertThrows(PollNotOpenException.class, () ->
                pollService.voteOnPoll(user1.getUid(), option1.getId()));
        assertEquals(oldVotes, pollOptionRepository.findById(option1.getId()).getVotes());

        poll1.setOpen(true);
    }

    @Test
    public void voteOnPollUserNotInLectureTest() {
        userRepository.save(user2);
        long oldVotes = pollOptionRepository.findById(option1.getId()).getVotes();

        assertThrows(UserNotInLectureException.class, () ->
                pollService.voteOnPoll(user2.getUid(), option1.getId()));
        assertEquals(oldVotes, pollOptionRepository.findById(option1.getId()).getVotes());

        userRepository.deleteById(user2.getUid());
    }

    @Test
    public void voteOnPollPollAlreadyVotedTest() throws Exception {
        pollService.voteOnPoll(user1.getUid(), option1.getId());
        long oldVotes = pollOptionRepository.findById(option1.getId()).getVotes();
        int size = userPollVoteRepository.findAllByUserId(user1.getUid()).size();

        assertThrows(PollAlreadyVotedException.class, () ->
                pollService.voteOnPoll(user1.getUid(), option1.getId()));
        assertEquals(oldVotes, pollOptionRepository.findById(option1.getId()).getVotes());
        assertEquals(size, userPollVoteRepository.findAllByUserId(user1.getUid()).size());

        userPollVoteRepository.deleteAllByOptionId(option1.getId());
    }

    @Test
    public void voteOnPollSuccessfulTest() throws Exception {
        long oldVotes = pollOptionRepository.findById(option1.getId()).getVotes();
        int size = userPollVoteRepository.findAllByUserId(user1.getUid()).size();

        assertEquals(0, pollService.voteOnPoll(user1.getUid(), option1.getId()));
        assertEquals(oldVotes + 1, pollOptionRepository.findById(option1.getId()).getVotes());
        assertEquals(size + 1, userPollVoteRepository.findAllByUserId(user1.getUid()).size());

        userPollVoteRepository.deleteAllByOptionId(option1.getId());
    }

    /**
     * Tests for fetchPollAndOptions for student.
     */

    @Test
    public void fetchPollAndOptionsStudentNoLectureTest() {
        assertThrows(LectureNotFoundException.class, () ->
                pollService.fetchPollAndOptionsStudent(l2.getUuid()));
    }

    @Test
    public void fetchPollAndOptionsStudentPollNotFoundTest() {
        pollRepository.deleteById(poll1.getId());

        assertThrows(PollNotFoundException.class, () ->
                pollService.fetchPollAndOptionsStudent(l1.getUuid()));

        pollRepository.save(poll1);
    }

    @Test
    public void fetchPollAndOptionsStudentSuccessfulTest() throws Exception {
        PollAndOptions pollAndOptions = pollService.fetchPollAndOptionsStudent(l1.getUuid());
        assertNotNull(pollAndOptions);
        assertTrue(pollAndOptions.getOptions().size() > 0);

        pollAndOptions.getOptions().forEach(o -> {
            assertEquals(0, o.getVotes());
            assertFalse(o.isCorrect());
        });
    }

    /**
     * Tests for fetchPollAndOptions for lecturer.
     */

    @Test
    public void fetchPollAndOptionsLecturerNoLectureTest() {
        assertThrows(LectureNotFoundException.class, () ->
                pollService.fetchPollAndOptionsLecturer(l2.getUuid(), l1.getModkey()));
    }

    @Test
    public void fetchPollAndOptionsLecturerIncorrectModkeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                pollService.fetchPollAndOptionsLecturer(l1.getUuid(), l2.getModkey()));
    }

    @Test
    public void fetchPollAndOptionsLecturerPollNotFoundTest() {
        pollRepository.deleteById(poll1.getId());

        assertThrows(PollNotFoundException.class, () ->
                pollService.fetchPollAndOptionsLecturer(l1.getUuid(), l1.getModkey()));

        pollRepository.save(poll1);
    }

    @Test
    public void fetchPollAndOptionsLecturerSuccessfulTest() throws Exception {
        PollAndOptions pollAndOptions =
                pollService.fetchPollAndOptionsLecturer(l1.getUuid(), l1.getModkey());

        assertNotNull(pollAndOptions);
        assertTrue(pollAndOptions.getOptions().size() > 0);

        pollAndOptions.getOptions().forEach(o -> assertNotEquals(0, o.getVotes()));
    }

    /**
     * Tests for reset votes.
     */

    @Test
    public void resetVotesNoLectureTest() {
        userRepository.save(user2);
        pollRepository.save(poll2);

        assertThrows(LectureNotFoundException.class, () ->
                pollService.resetVotes(poll2.getId(), l2.getModkey()));
        List<PollOptionEntity> options = pollOptionRepository.findAllByPollId(poll2.getId());
        options.forEach(o -> assertTrue(o.getVotes() > 0));

        userRepository.deleteById(user2.getUid());
        pollRepository.deleteById(poll2.getId());
    }

    @Test
    public void resetVotesPollNotFoundTest() {
        lectureRepository.save(l2);

        assertThrows(PollNotFoundException.class, () ->
                pollService.resetVotes(poll2.getId(), l2.getModkey()));
        List<PollOptionEntity> options = pollOptionRepository.findAllByPollId(poll2.getId());
        options.forEach(o -> assertTrue(o.getVotes() > 0));

        lectureRepository.deleteById(l2.getUuid());
    }

    @Test
    public void resetVotesIncorrectModkeyTest() {
        assertThrows(InvalidModkeyException.class, () ->
                pollService.resetVotes(poll1.getId(), l2.getModkey()));
        List<PollOptionEntity> options = pollOptionRepository.findAllByPollId(poll1.getId());
        options.forEach(o -> assertTrue(o.getVotes() > 0));
    }

    @Test
    public void resetVotesSuccessfulTest() throws Exception {
        assertEquals(0, pollService.resetVotes(poll1.getId(), l1.getModkey()));
        List<PollOptionEntity> options = pollOptionRepository.findAllByPollId(poll1.getId());
        options.forEach(o -> assertEquals(0,o.getVotes()));
    }
}