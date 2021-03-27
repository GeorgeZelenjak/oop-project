package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.entities.poll.UserPollVoteTable;
import nl.tudelft.oopp.livechat.repositories.*;
import nl.tudelft.oopp.livechat.services.PollService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for Poll controller tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PollControllerTest {
    private static final Timestamp time = new Timestamp(System.currentTimeMillis());
    private static final long uid1 = 7685676369123504312L;
    private static final long uid2 = 5497688963356625764L;

    private static LectureEntity lecture1;
    private static LectureEntity lecture2;

    private static UserEntity user1;
    private static UserEntity user2;

    private static PollEntity poll1;
    private static PollEntity poll2;
    private static PollOptionEntity option1;
    private static PollOptionEntity option2;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private PollService pollService;

    /**
     * Setup for the tests.
     */
    @BeforeAll
    public static void setUp() {
        lecture1 = new LectureEntity("Nutrition I", "Alien1", time);
        lecture2 = new LectureEntity("Nutrition II", "Alien2", time);

        user1 = new UserEntity(uid1, "Baby alien 1", new Timestamp(
                System.currentTimeMillis()), true,
                "127.0.0.1", lecture1.getUuid());

        user2 = new UserEntity(uid2, "Baby alien 1", new Timestamp(
                System.currentTimeMillis()), true,
                "127.0.0.1", lecture2.getUuid());

        poll1 = new PollEntity(lecture1.getUuid(),"What animal will we eat for first breakfast?",
                time, 32, true);
        poll2 = new PollEntity(lecture2.getUuid(),"What animal will we eat for second breakfast?",
                time, 23, false);

        option1 = new PollOptionEntity(poll1.getId(), "Whale", 5, false);
        option2 = new PollOptionEntity(poll1.getId(), "Dolphin", 13, true);
    }

    /**
     * Setup before each test (adds some entities to the repository).
     */
    @BeforeEach
    public void setup() {
        lectureRepository.save(lecture1);
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
        lectureRepository.deleteById(lecture1.getUuid());
        userRepository.deleteById(user1.getUid());

        pollRepository.deleteAll();
        pollOptionRepository.deleteAll();
    }

    @Test
    public void createPollSuccessfulTest() throws Exception {
        String result = this.mockMvc
                .perform(post("/api/poll/create/" + lecture1.getUuid() + "/" + lecture1.getModkey())
                        .contentType(APPLICATION_JSON).content("What will we eat for lunch?")
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        PollEntity p = objectMapper.readValue(result, PollEntity.class);
        assertNotNull(p);
        assertEquals("What will we eat for lunch?", p.getQuestionText());
        assertEquals(lecture1.getUuid(), p.getLectureId());
    }

    @Test
    public void createPollInvalidModkeyTest() throws Exception {
        String result = this.mockMvc
                .perform(post("/api/poll/create/" + lecture1.getUuid() + "/" + lecture2.getModkey())
                        .contentType(APPLICATION_JSON).content("What will we eat for lunch?")
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);
    }

    @Test
    public void createPollNoLectureTest() throws Exception {
        String result = this.mockMvc
                .perform(post("/api/poll/create/" + lecture2.getUuid() + "/" + lecture2.getModkey())
                        .contentType(APPLICATION_JSON).content("What will we eat for lunch?")
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);
    }

    @Test
    public void addOptionSuccessfulTest() throws Exception {
        String result = this.mockMvc
                .perform(post("/api/poll/addOption/" + poll1.getId()
                        + "/" + lecture1.getModkey() + "/" + true)
                        .contentType(APPLICATION_JSON).content("Babies")
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        PollOptionEntity o = objectMapper.readValue(result, PollOptionEntity.class);
        assertNotNull(o);
        assertEquals("Babies", o.getOptionText());
        assertEquals(poll1.getId(), o.getPollId());
    }

    @Test
    public void addOptionPollNotFoundTest() throws Exception {
        String result = this.mockMvc
                .perform(post("/api/poll/addOption/" + poll2.getId()
                        + "/" + lecture1.getModkey() + "/" + true)
                        .contentType(APPLICATION_JSON).content("Monsters")
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll does not exist", result);
    }

    @Test
    public void addOptionWrongModKeyTest() throws Exception {
        String result = this.mockMvc
                .perform(post("/api/poll/addOption/" + poll1.getId()
                        + "/" + lecture2.getModkey() + "/" + true)
                        .contentType(APPLICATION_JSON).content("Monsters")
                        .characterEncoding("utf-8"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);
    }

    @Test
    public void addOptionLectureNotFoundTest() throws Exception {
        lectureRepository.deleteById(lecture1.getUuid());
        String result = this.mockMvc
                .perform(post("/api/poll/addOption/" + poll1.getId()
                        + "/" + lecture1.getModkey() + "/" + true)
                        .contentType(APPLICATION_JSON).content("Monsters")
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);

        lectureRepository.save(lecture1);
    }

    @Test
    public void toggleSuccessfulTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/toggle/" + poll1.getId()
                        + "/" + lecture1.getModkey()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals("0", result);
        PollEntity p = pollRepository.findById(poll1.getId());
        assertNotNull(p);
        assertFalse(p.isOpen());
    }

    @Test
    public void toggleLectureNotFoundTest() throws Exception {
        lectureRepository.deleteById(lecture1.getUuid());
        String result = this.mockMvc
                .perform(put("/api/poll/toggle/" + poll1.getId()
                        + "/" + lecture1.getModkey()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);
        lectureRepository.save(lecture1);
    }

    @Test
    public void toggleWrongModKeyTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/toggle/" + poll1.getId()
                        + "/" + lecture2.getModkey()))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);
    }

    @Test
    public void togglePollNotFoundTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/toggle/" + poll2.getId()
                        + "/" + lecture1.getModkey()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll does not exist", result);
    }

    @Test
    public void fetchPollAndOptionsStudentSuccessfulTest() throws Exception {
        String result = this.mockMvc
                .perform(get("/api/poll/fetchStudent/" + lecture1.getUuid()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PollAndOptions p = objectMapper.readValue(result, PollAndOptions.class);
        assertNotNull(p);
        assertEquals(poll1.getQuestionText(), p.getPoll().getQuestionText());
        p.getOptions().forEach(o -> {
            assertEquals(0, o.getVotes());
            assertFalse(o.isCorrect());
            assertTrue(option1.getOptionText().equals(o.getOptionText())
                || option2.getOptionText().equals(o.getOptionText()));
        });
    }

    @Test
    public void fetchPollAndOptionsStudentNoLectureTest() throws Exception {
        String result = this.mockMvc
                .perform(get("/api/poll/fetchStudent/" + lecture2.getUuid()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);
    }

    @Test
    public void fetchPollAndOptionsStudentNoPollTest() throws Exception {
        pollRepository.deleteById(poll1.getId());
        String result = this.mockMvc
                .perform(get("/api/poll/fetchStudent/" + lecture1.getUuid()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll does not exist", result);

        pollRepository.save(poll1);
    }

    @Test
    public void fetchPollAndOptionsLecturerSuccessfulTest() throws Exception {
        String result = this.mockMvc
                .perform(get("/api/poll/fetchMod/" + lecture1.getUuid()
                        + "/" + lecture1.getModkey()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PollAndOptions p = objectMapper.readValue(result, PollAndOptions.class);
        assertNotNull(p);
        assertEquals(poll1.getQuestionText(), p.getPoll().getQuestionText());
        p.getOptions().forEach(o -> {
            assertNotEquals(0, o.getVotes());
            assertTrue(option1.getOptionText().equals(o.getOptionText())
                    || option2.getOptionText().equals(o.getOptionText()));
        });
    }

    @Test
    public void fetchPollAndOptionsLecturerNoPollTest() throws Exception {
        pollRepository.deleteById(poll1.getId());
        String result = this.mockMvc
                .perform(get("/api/poll/fetchMod/" + lecture1.getUuid()
                        + "/" + lecture1.getModkey()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll does not exist", result);

        pollRepository.save(poll1);
    }

    @Test
    public void fetchPollAndOptionsLecturerNoLectureTest() throws Exception {
        String result = this.mockMvc
                .perform(get("/api/poll/fetchMod/" + lecture2.getUuid()
                        + "/" + lecture1.getModkey()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);
    }

    @Test
    public void fetchPollAndOptionsLecturerWrongModKeyTest() throws Exception {
        String result = this.mockMvc
                .perform(get("/api/poll/fetchMod/" + lecture1.getUuid()
                        + "/" + lecture2.getModkey()))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);
    }

    @Test
    public void voteOnPollSuccessfulTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/vote/" + user1.getUid()
                        + "/" + option1.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals("0", result);
        PollOptionEntity o = pollOptionRepository.findById(option1.getId());
        assertNotNull(o);
        assertEquals(option1.getVotes() + 1, o.getVotes());
    }

    @Test
    public void voteOnPollNotRegisteredTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/vote/" + user2.getUid()
                        + "/" + option1.getId()))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This user is not registered", result);
        PollOptionEntity o = pollOptionRepository.findById(option1.getId());
        assertNotNull(o);
        assertEquals(option1.getVotes(), o.getVotes());
    }

    @Test
    public void voteOnPollNoPollOptionTest() throws Exception {
        pollOptionRepository.deleteById(option1.getId());

        String result = this.mockMvc
                .perform(put("/api/poll/vote/" + user1.getUid()
                        + "/" + option1.getId()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll option does not exist", result);
    }

    @Test
    public void voteOnPollNoPollTest() throws Exception {
        pollRepository.deleteById(poll1.getId());

        String result = this.mockMvc
                .perform(put("/api/poll/vote/" + user1.getUid()
                        + "/" + option1.getId()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll does not exist", result);
    }

    @Test
    public void voteOnPollNotInTheLectureTest() throws Exception {
        user1.setLectureId(lecture2.getUuid());
        userRepository.save(user1);

        String result = this.mockMvc
                .perform(put("/api/poll/vote/" + user1.getUid()
                        + "/" + option1.getId()))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This user is not in the specified lecture", result);

        user1.setLectureId(lecture1.getUuid());
    }

    @Test
    public void voteOnPollAlreadyVotedTest() throws Exception {
        userPollVoteRepository.save(new UserPollVoteTable(user1.getUid(),
                option1.getId(), poll1.getId()));
        String result = this.mockMvc
                .perform(put("/api/poll/vote/" + user1.getUid()
                        + "/" + option1.getId()))
                .andExpect(status().isLocked())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("You have already voted in this poll", result);
        PollOptionEntity o = pollOptionRepository.findById(option1.getId());
        assertNotNull(o);
        assertEquals(option1.getVotes(), o.getVotes());

        userPollVoteRepository.deleteAllByOptionId(option1.getId());
    }

    @Test
    public void voteOnPollNotOpenTest() throws Exception {
        poll1.setOpen(false);
        pollRepository.save(poll1);

        String result = this.mockMvc
                .perform(put("/api/poll/vote/" + user1.getUid()
                        + "/" + option1.getId()))
                .andExpect(status().isLocked())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll is not open", result);
        PollOptionEntity o = pollOptionRepository.findById(option1.getId());
        assertNotNull(o);
        assertEquals(option1.getVotes(), o.getVotes());

        poll1.setOpen(true);
    }

    @Test
    public void resetVotesSuccessfulTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/reset/" + poll1.getId() + "/" + lecture1.getModkey()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals("0", result);
        PollEntity p = pollRepository.findById(poll1.getId());
        assertNotNull(p);
        assertEquals(0, p.getVotes());
    }

    @Test
    public void resetVotesPollNotFoundTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/reset/" + poll2.getId() + "/" + lecture1.getModkey()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This poll does not exist", result);
    }

    @Test
    public void resetVotesLectureNotFoundTest() throws Exception {
        lectureRepository.deleteById(lecture1.getUuid());
        String result = this.mockMvc
                .perform(put("/api/poll/reset/" + poll1.getId() + "/" + lecture1.getModkey()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);
        PollEntity p = pollRepository.findById(poll1.getId());
        assertNotNull(p);
        assertNotEquals(0, p.getVotes());

        lectureRepository.save(lecture1);
    }

    @Test
    public void resetVotesWrongModKeyTest() throws Exception {
        String result = this.mockMvc
                .perform(put("/api/poll/reset/" + poll1.getId() + "/" + lecture2.getModkey()))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);
    }

    @Test
    public void badUUIDTest() throws Exception {
        String result = this.mockMvc
                .perform(post("/api/poll/create/soValidUUID/" + lecture1.getModkey())
                        .contentType(APPLICATION_JSON).content("What will we eat for lunch?")
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("UUID is not in the correct format", result);
    }
}
