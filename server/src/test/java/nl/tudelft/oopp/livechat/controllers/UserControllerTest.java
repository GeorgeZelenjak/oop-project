package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String user1Json;
    private static String user2Json;
    private static String user3Json;


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
     *
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
        objectMapper.registerModule(new JavaTimeModule());
        UserEntity user1 = new UserEntity(createUid(), "root",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000),
                true, "192.168.1.1", UUID.randomUUID());
        user1Json = objectMapper.writeValueAsString(user1);

        UserEntity user2 = new UserEntity(420, "sudo",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000),
                true, "127.0.0.1", UUID.randomUUID());
        user2Json = objectMapper.writeValueAsString(user2);

        UserEntity user3 = new UserEntity(createUid(), "sudo rm -rf /",
                new Timestamp(System.currentTimeMillis() / 1000 * 1000),
                true, "127.0.0.1", null);
        user3Json = objectMapper.writeValueAsString(user3);

        JsonNode node = objectMapper.readTree(user3Json);
        ((ObjectNode) node).remove("lectureId");
        node = ((ObjectNode) node).put("lectureId", "validUUID");
        user3Json = node.toString();
    }

    @Test
    public void newUserSuccessfulTest() throws Exception {
        String result = this.mockMvc.perform(post("/api/user/register")
                .contentType(APPLICATION_JSON).content(user1Json).characterEncoding("utf-8"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        assertEquals(0, Integer.parseInt(result));
    }

    @Test
    public void newUserUnsuccessfulTest() throws Exception {
        String result = this.mockMvc.perform(post("/api/user/register")
                .contentType(APPLICATION_JSON).content(user2Json).characterEncoding("utf-8"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        assertEquals(-1, Integer.parseInt(result));
    }

    @Test
    public void newUserInvalidUUIDTest() throws Exception {
        String result = this.mockMvc.perform(post("/api/user/register")
                .contentType(APPLICATION_JSON).content(user3Json).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Invalid UUID", result);
    }



}
