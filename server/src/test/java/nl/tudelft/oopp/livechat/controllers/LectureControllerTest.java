package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@SpringBootTest
@AutoConfigureMockMvc
class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getLecturesByID() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        MvcResult result = this.mockMvc.perform(post("/post?name=test1"))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        LectureEntity lectureEntity = objectMapper.readValue(json, LectureEntity.class);
        String uuid = lectureEntity.getUuid();
        String m = this.mockMvc.perform(get("/get/" + uuid)).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(json, m);
        assertEquals(lectureEntity.getName(), "test1");
        assertEquals(lectureEntity.getCreatorName(), "placeholder");
    }

    @Test
    void whenPosting_thenReturns200() throws Exception {
        this.mockMvc.perform(post("/post?name=test2")).andExpect(status().isOk());
    }

    @Test
    void whenGetting_returns404() throws Exception {
        this.mockMvc.perform(get("/get")).andExpect(status().is4xxClientError());
    }


}

