package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;



@SpringBootTest
@AutoConfigureMockMvc
class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**.
     * A method to create a lecture.
     * @param url url with lecture name
     * @return JSON representation of the new lecture entity
     * @throws Exception if something goes wrong
     */
    String createLecture(String url) throws Exception {
        return this.mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**.
     * A method to get a lecture.
     * @param url url with lecture id
     * @return JSON representation of the new lecture entity
     * @throws Exception if something goes wrong
     */
    String getLecture(String url) throws Exception {
        return this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**.
     * A method to delete a lecture.
     * @param url url with lecture id and moderator id values
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int deleteLecture(String url) throws Exception {
        String m = this.mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return Integer.parseInt(m);
    }

    @Test
    void createLectureTest() {
        assertDoesNotThrow(() -> createLecture("/api/newLecture?name=test1"));
    }

    @Test
    void getLecturesByIDTest() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        String json = createLecture("/api/newLecture?name=test1");
        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);

        String uuid = lectureEntity.getUuid().toString();
        String m = getLecture("/api/get/" + uuid);
        assertEquals(json, m);
        assertEquals(lectureEntity.getName(), "test1");
        assertEquals(lectureEntity.getCreatorName(), "placeholder");
    }

    @Test
    void whenPostingReturns200Test() throws Exception {
        this.mockMvc.perform(post("/api/newLecture?name=test2"))
                .andExpect(status().isOk());
    }

    @Test
    void whenGettingReturns404Test() throws Exception {
        this.mockMvc.perform(get("/api/get"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenGettingReturns400Test() throws Exception {
        this.mockMvc.perform(get("/api/get/notauuid"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteLectureSuccessfulTest() throws Exception {
        String json = createLecture("/api/newLecture?name=test3");

        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();
        String modkey = lectureEntity.getModkey().toString();

        int m = deleteLecture("/api/delete/" + uuid + "/" + modkey);
        assertEquals(0, m);

        String lecture = getLecture("/api/get/" + uuid);
        assertEquals("", lecture);

        m = deleteLecture("/api/delete/" + uuid + "/" + modkey);
        assertEquals(-1, m);
    }

    @Test
    void deleteLectureUnsuccessfulTest() throws Exception {
        String json = createLecture("/api/newLecture?name=test3");

        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();

        int m = deleteLecture("/api/delete/" + uuid + "/" + UUID.randomUUID().toString());
        assertEquals(-1, m);

        String lecture = getLecture("/api/get/" + uuid);
        assertNotEquals("", lecture);
    }
}

