package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.UserLectureSpeedRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private static long uid1;
    private static long uid2;
    private static long uid3;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        LectureEntity currentLecture = new LectureEntity();
        lectureRepository.save(currentLecture);
        uuid = currentLecture.getUuid();
        UserEntity user1 = new UserEntity(1,"User",
                new Timestamp(System.currentTimeMillis()),true,"Is",uuid);
        UserEntity user2 = new UserEntity(2,"Billy",
                new Timestamp(System.currentTimeMillis()),true,"Mayonnaise",uuid);
        UserEntity user3 = new UserEntity(2,"Mandy",
                new Timestamp(System.currentTimeMillis()),true,"Instrument",uuid);
        uid1 = user1.getUid();
        uid2 = user2.getUid();
        uid3 = user3.getUid();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }


    @Test
    public void voteOnLectureSpeedRespondsAllCorrect() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/vote/lectureSpeed?uid="
                + uid1 + "&uuid=" + uuid)
                .content("slower")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("0",result.getResponse().getContentAsString());
    }

    @Test
    public void voteOnLectureSpeedWrongUuid() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/vote/lectureSpeed?uid=" + uid1 + "&uuid="
                + UUID.randomUUID())
                .content("slower")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("-1",result.getResponse().getContentAsString());
    }

}