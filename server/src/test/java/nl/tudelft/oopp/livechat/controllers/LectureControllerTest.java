package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
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

    /**
     * A method to delete a lecture.
     * @param url url with lecture id and moderator key values
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int deleteLecture(String url) throws Exception {
        String result = this.mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return Integer.parseInt(result);
    }

    /**
     * A method to close a lecture.
     * @param url url with lecture id and moderator key values
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int closeLecture(String url) throws Exception {
        String result = this.mockMvc.perform(put(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return Integer.parseInt(result);
    }

    /**
     * A method to validate a moderator key.
     * @param url url with lecture id and moderator key values
     * @return 0 if successful, otherwise -1
     * @throws Exception if something goes wrong
     */
    int validateModkey(String url) throws Exception {
        String result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return Integer.parseInt(result);
    }

    @Test
    void createLectureTest() {
        assertDoesNotThrow(() -> createLecture("/api/newLecture?name=test1"));
    }

    @Test
    void createLectureModkeyTest() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        String json = createLecture("/api/newLecture?name=test2");
        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        assertNotNull(lectureEntity);
        assertNotNull(lectureEntity.getModkey());
    }

    @Test
    void getLecturesByIDSuccessfulTest() throws Exception {
        String json = createLecture("/api/newLecture?name=test1");
        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);

        String uuid = lectureEntity.getUuid().toString();
        String m = getLecture("/api/get/" + uuid);
        assertNotEquals(json, m);

        LectureEntity gotBack = objectMapper.readValue(m, LectureEntity.class);
        assertEquals(lectureEntity.getName(), "test1");
        assertEquals(lectureEntity.getCreatorName(), "placeholder");
        assertNull(gotBack.getModkey());
        assertEquals(gotBack, lectureEntity);
    }

    @Test
    void getLecturesByIDUnsuccessfulTest() throws Exception {
        createLecture("/api/newLecture?name=test1");

        String uuid = UUID.randomUUID().toString();
        String m = getLecture("/api/get/" + uuid);
        assertEquals("", m);
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
        this.mockMvc.perform(get("/api/get/not-a-uuid"))
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

    @Test
    void validateModkeySuccessfulTest() throws Exception {
        String json = createLecture("/api/newLecture?name=ads");
        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();
        String modkey = lectureEntity.getModkey().toString();

        int m = validateModkey("/api/validate/" + uuid + "/" + modkey);
        assertEquals(0, m);
    }

    @Test
    void validateModkeyUnsuccessfulTest() throws Exception {
        String json = createLecture("/api/newLecture?name=ads");
        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();
        String modkey = UUID.randomUUID().toString();

        int m = validateModkey("/api/validate/" + uuid + "/" + modkey);
        assertEquals(-1, m);
    }

    @Test
    void closeLectureSuccessfulTest() throws Exception {
        String json = createLecture("/api/newLecture?name=test4");

        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();
        String modkey = lectureEntity.getModkey().toString();

        int m = closeLecture("/api/close/" + uuid + "/" + modkey);
        assertEquals(0, m);

        String lecture = getLecture("/api/get/" + uuid);
        LectureEntity l = objectMapper.readValue(lecture, LectureEntity.class);
        assertFalse(l.isOpen());
    }

    @Test
    void closeLectureUnsuccessfulTest() throws Exception {
        String json = createLecture("/api/newLecture?name=test4");

        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();

        int m = closeLecture("/api/close/" + uuid + "/" + UUID.randomUUID().toString());
        assertEquals(-1, m);
    }

    @Test
    void validateModkeyUnsuccessfulNoLectureTest() throws Exception {
        String json = createLecture("/api/newLecture?name=ads");
        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();
        String modkey = lectureEntity.getModkey().toString();

        deleteLecture("/api/delete/" + uuid + "/" + modkey);

        int m = validateModkey("/api/validate/" + uuid + "/" + modkey);
        assertEquals(-1, m);
    }


    @Test
    void closeLectureNoLectureTest() throws Exception {
        String json = createLecture("/api/newLecture?name=test4");

        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid().toString();

        deleteLecture("/api/delete/" + uuid + "/" + lectureEntity.getModkey().toString());

        int m = closeLecture("/api/close/" + uuid
                + "/" + lectureEntity.getModkey().toString());
        assertEquals(-1, m);
    }

    @Test
    void exceptionHandlerTest() throws Exception {
        String json = createLecture("/api/newLecture?name=ads");

        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String modkey = lectureEntity.getModkey().toString();

        String result = this.mockMvc
                .perform(delete("/api/delete/my hands are writing words/" + modkey))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Invalid UUID", result);
    }
}

