package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
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
    public void voteOnLectureSlowerSpeedCorrectTest() throws Exception {
        String result = vote("/api/vote/lectureSpeed?uid="
                    + uid1 + "&uuid=" + uuid, "slower");
        assertEquals("0", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureFasterSpeedCorrectTest() throws Exception {
        String result = vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        assertEquals("0", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureTwiceSameTest() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        String result = vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        assertEquals("0", result);
        assertTrue(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureTwiceDifferentTest() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        String result = vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "slower");
        assertEquals("0", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureSpeedWrongUuidTest() throws Exception {
        String result = mockMvc.perform(put("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + UUID.randomUUID())
                .content("faster")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("This user is not in the specified lecture", result);
        assertTrue(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void voteOnLectureInvalidUuidTest() throws Exception {
        String result = mockMvc.perform(put("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=validUUID")
                .content("faster")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("UUID is not in the correct format", result);
        assertTrue(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void resetSuccessfulTest() throws Exception {
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
    public void resetWrongModKeyTest() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        vote("/api/vote/lectureSpeed?uid="
                + uid2 + "&uuid=" + uuid, "slower");

        String result = mockMvc.perform(delete(("/api/vote/resetLectureSpeedVote/"
                + uuid + "/" + uuid)))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Wrong modkey, don't do this", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void resetInvalidUUIDTest() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        vote("/api/vote/lectureSpeed?uid="
                + uid2 + "&uuid=" + uuid, "slower");

        String result = mockMvc.perform(delete(("/api/vote/resetLectureSpeedVote/correctUUID"
                + "/" + lecture.getModkey())))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals("UUID is not in the correct format", result);
        assertFalse(speedRepository.findAllByLectureId(uuid).isEmpty());
    }

    @Test
    public void getVotesSuccessfulTest() throws Exception {
        vote("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid, "faster");
        vote("/api/vote/lectureSpeed?uid="
                + uid2 + "&uuid=" + uuid, "slower");

        String result = mockMvc.perform(get(("/api/vote/getLectureSpeed/"
                + "/" + lecture.getUuid())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> list = objectMapper.readValue(result,
                new TypeReference<>(){});
        assertEquals(List.of(1,1), list);
    }

    @Test
    public void getVotesUnsuccessfulTest() throws Exception {
        lectureRepository.deleteById(uuid);

        String result = mockMvc.perform(get(("/api/vote/getLectureSpeed/"
                + "/" + uuid))).andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);

        lectureRepository.save(lecture);
    }
}