package nl.tudelft.oopp.livechat.controllers;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;

import static org.junit.jupiter.api.Assertions.*;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.UserLectureSpeedRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Class for Lecture Controller tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserLectureVotingControllerTest {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLectureSpeedRepository speedRepository;

    private static UUID uuid;
    private static final long uid1 = 526613476247652L;
    private static final long uid2 = 47687683243663L;
    private static LectureEntity lecture;


    private static UserEntity user1;
    private static UserEntity user2;

    @Autowired
    private MockMvc mockMvc;

    /**
     * A helper method to vote.
     * @param path the path
     * @param vote the speed preference
     * @return 0 if successful, -1 otherwise
     * @throws Exception if something goes wrong
     */
    private String vote(String path, String vote) throws Exception {
        return mockMvc.perform(put(path)
                .content(vote)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @BeforeAll
    static void setup() {
        lecture = new LectureEntity();
        uuid = lecture.getUuid();
        user1 = new UserEntity(uid1,"Linus",
                new Timestamp(System.currentTimeMillis()),true,"Hidden", uuid);
        user2 = new UserEntity(uid2,"Linus",
                new Timestamp(System.currentTimeMillis()),true,"localhost", uuid);
    }

    @BeforeEach
    void setUp() {
        lectureRepository.save(lecture);
        uuid = lecture.getUuid();

        userRepository.save(user1);
        userRepository.save(user2);

        speedRepository.deleteAllByLectureId(lecture.getUuid());
    }

    @AfterEach
    public void clear() {
        userRepository.deleteById(uid1);
        userRepository.deleteById(uid2);
    }


    @Test
    public void voteOnLectureSlowerSpeedCorrect() throws Exception {
        String result = vote("/api/vote/lectureSpeed?uid="
                    + uid1 + "&uuid=" + uuid, "slower");
        assertEquals("0", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureFasterSpeedCorrect() throws Exception {
        String result = vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        assertEquals("0", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureTwiceSame() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        String result = vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        assertEquals("0", result);
        assertTrue(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureTwiceDifferent() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        String result = vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "slower");
        assertEquals("0", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureSpeedWrongUuid() throws Exception {
        String result = vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + UUID.randomUUID(), "faster");
        assertEquals("-1", result);
        assertTrue(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureInvalidUuid() throws Exception {
        String result = mockMvc.perform(put("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=validUUID", "faster")
                .content("faster")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Don't do this", result);
        assertTrue(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void resetSuccessful() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        vote("/api/vote/lectureSpeed?uid="
                + uid2 + "&uuid=" + uuid, "slower");

        String result = mockMvc.perform(delete(("/api/vote/resetLectureSpeedVote/"
                + uuid + "/" + lecture.getModkey())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals("0", result);
        assertTrue(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void resetWrongModKey() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        vote("/api/vote/lectureSpeed?uid="
                + uid2 + "&uuid=" + uuid, "slower");

        String result = mockMvc.perform(delete(("/api/vote/resetLectureSpeedVote/"
                + uuid + "/" + uuid)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals("-1", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void resetInvalidUUID() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        vote("/api/vote/lectureSpeed?uid="
                + uid2 + "&uuid=" + uuid, "slower");

        String result = mockMvc.perform(delete(("/api/vote/resetLectureSpeedVote/correctUUID"
                + "/" + lecture.getModkey())))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("Don't do this", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }
}