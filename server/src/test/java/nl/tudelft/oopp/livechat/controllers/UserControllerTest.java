package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private static ObjectMapper objectMapper;

    private static String user1Json;
    private static String user2Json;
    private static String user3Json;

    private static UserEntity user1;
    private static UserEntity user2;
    private static UserEntity user3;

    private static QuestionEntity q1;
    private static QuestionEntity q2;
    private static QuestionEntity q3;

    private static LectureEntity l1;
    private static LectureEntity l2;

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
        for (int i = 0;i < hardwareAddress.length; i++) {
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


    /**
     * Setup for the class.
     * @throws Exception if something goes wrong
     */
    @BeforeAll
    public static void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        l1 = new LectureEntity("Call of Duty", "Bingo", new Timestamp(System.currentTimeMillis()));
        l2 = new LectureEntity("CS", "Bongo", new Timestamp(System.currentTimeMillis()));

        user1 = new UserEntity(createUid(), "root",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000),
                true, "192.168.1.1", l1.getUuid());
        user1Json = objectMapper.writeValueAsString(user1);

        user2 = new UserEntity(420, "sudo",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000),
                true, "127.0.0.1", l2.getUuid());
        user2Json = objectMapper.writeValueAsString(user2);

        user3 = new UserEntity(342534544534560097L, "sudo rm -rf /",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000),
                true, "127.0.0.1", l2.getUuid());
        user3Json = objectMapper.writeValueAsString(user3);

        JsonNode node = objectMapper.readTree(user3Json);
        ((ObjectNode) node).remove("lectureId");
        node = ((ObjectNode) node).put("lectureId", "validUUID");
        user3Json = node.toString();

        q1 = new QuestionEntity(l1.getUuid(), "What?",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000), user1.getUid());
        q2 = new QuestionEntity(l2.getUuid(), "Where?",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000), user2.getUid());
        q3 = new QuestionEntity(l2.getUuid(), "When?",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000), user3.getUid());

    }

    @Test
    public void newUserSuccessfulTest() throws Exception {
        String result = this.mockMvc.perform(post("/api/user/register")
                .contentType(APPLICATION_JSON).content(user1Json).characterEncoding("utf-8"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        assertEquals(0, Integer.parseInt(result));

        userRepository.deleteById(user1.getUid());
    }

    @Test
    public void newUserUnsuccessfulTest() throws Exception {
        String result = this.mockMvc.perform(post("/api/user/register")
                .contentType(APPLICATION_JSON).content(user2Json).characterEncoding("utf-8"))
                .andExpect(status().is4xxClientError()).andReturn()
                .getResponse().getErrorMessage();
        assertEquals("Invalid user id, don't do this", result);
    }

    @Test
    public void newUserInvalidUUIDTest() throws Exception {
        System.out.println(user3Json);
        String result = this.mockMvc.perform(post("/api/user/register")
                .contentType(APPLICATION_JSON).content(user3Json).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("UUID is not in the correct format", result);
    }

    @Test
    public void banByIdSuccessfulTest() throws Exception {
        lectureRepository.save(l1);
        userRepository.save(user1);
        userRepository.save(user3);
        questionRepository.save(q1);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("modid", user3.getUid());
        node.put("qid", q1.getId());
        node.put("modkey", l1.getModkey().toString());
        node.put("time", 2);
        String jason = node.toString();

        String result = this.mockMvc.perform(put("/api/user/ban/id")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        assertEquals("0", result);

        userRepository.deleteById(user1.getUid());
        userRepository.deleteById(user3.getUid());
        lectureRepository.deleteById(l1.getUuid());
    }

    @Test
    public void banByIdUnsuccessfulTest() throws Exception {
        userRepository.save(user1);
        userRepository.save(user3);
        questionRepository.save(q1);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("modid", user3.getUid());
        node.put("qid", q1.getId());
        node.put("modkey", l1.getModkey().toString());
        node.put("time", 2);
        String jason = node.toString();

        String result = this.mockMvc.perform(put("/api/user/ban/id")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isNotFound()).andReturn()
                .getResponse().getErrorMessage();
        assertEquals("Lecture not found", result);

        userRepository.deleteById(user1.getUid());
        userRepository.deleteById(user3.getUid());
    }

    @Test
    public void banByIpSuccessfulTest() throws Exception {
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        questionRepository.save(q2);
        questionRepository.save(q3);
        lectureRepository.save(l2);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("modid", user1.getUid());
        node.put("qid", q3.getId());
        node.put("modkey", l2.getModkey().toString());
        node.put("time", 3);
        String jason = node.toString();

        String result = this.mockMvc.perform(put("/api/user/ban/ip")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        assertEquals("0", result);

        userRepository.deleteById(user1.getUid());
        userRepository.deleteById(user2.getUid());
        userRepository.deleteById(user3.getUid());
        questionRepository.deleteById(q2.getId());
        lectureRepository.deleteById(l2.getUuid());
    }

    @Test
    public void banByIpUnsuccessfulTest() throws Exception {
        userRepository.save(user1);
        questionRepository.save(q3);
        lectureRepository.save(l2);

        ObjectNode node = objectMapper.createObjectNode();
        node.put("modid", user1.getUid());
        node.put("qid", q3.getId());
        node.put("modkey", l2.getModkey().toString());
        node.put("time", 42);
        String jason = node.toString();

        String result = this.mockMvc.perform(put("/api/user/ban/ip")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isConflict()).andReturn()
                .getResponse().getErrorMessage();
        assertEquals("This user is not registered", result);

        userRepository.deleteById(user1.getUid());
        questionRepository.deleteById(q3.getId());
        lectureRepository.deleteById(l2.getUuid());
    }

    @Test
    public void negativeNumberIdTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("modid", user3.getUid());
        node.put("qid", q1.getId());
        node.put("modkey", l1.getModkey().toString());
        node.put("time", -2);
        String jason = node.toString();

        String result = this.mockMvc.perform(put("/api/user/ban/id")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest()).andReturn()
                .getResponse().getContentAsString();
        assertEquals("Don't do this", result);
    }

    @Test
    public void negativeNumberIpTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("modid", user1.getUid());
        node.put("qid", q1.getId());
        node.put("modkey", l1.getModkey().toString());
        node.put("time", -3);
        String jason = node.toString();

        String result = this.mockMvc.perform(put("/api/user/ban/ip")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest()).andReturn()
                .getResponse().getContentAsString();
        assertEquals("Don't do this", result);
    }

    @Test
    public void nullPointerTest() throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("modid", user3.getUid());
        String jason = node.toString();

        String result = this.mockMvc.perform(put("/api/user/ban/id")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest()).andReturn()
                .getResponse().getContentAsString();
        assertEquals("Don't do this", result);
    }

    @Test
    public void jsonFailTest() throws Exception {
        String jason = "{:\"44\",\"qid\":1995531438255504650}";
        String result = this.mockMvc.perform(put("/api/user/ban/id")
                .contentType(APPLICATION_JSON).content(jason).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest()).andReturn()
                .getResponse().getContentAsString();
        assertEquals("Don't do this", result);
    }

}
