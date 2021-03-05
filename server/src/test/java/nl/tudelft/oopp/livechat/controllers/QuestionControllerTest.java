package nl.tudelft.oopp.livechat.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {

    QuestionEntity q1;
    QuestionEntity q2;
    LectureEntity lectureEntity1;
    LectureEntity lectureEntity2;
    ObjectWriter ow;
    String q1Json;
    String q2Json;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        String lecture1 = this.mockMvc.perform(post("/api/newLecture?name=test1"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        lectureEntity1 = objectMapper.readValue(lecture1, LectureEntity.class);
        String lecture2 = this.mockMvc.perform(post("/api/newLecture?name=test2"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        lectureEntity2 = objectMapper.readValue(lecture2, LectureEntity.class);
        q1 = new QuestionEntity();
        q2 = new QuestionEntity();
        q1.setLectureId(lectureEntity1.getUuid());
        q2.setLectureId(lectureEntity2.getUuid());
        q1.setText("What would you do if a seagull entered in your house?");
        q2.setText("What would you do if a pelican entered in your house?");
        q1.setOwnerId(42);
        q2.setOwnerId(69);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = objectMapper.writer().withDefaultPrettyPrinter();
        q1Json = ow.writeValueAsString(q1);
        q2Json = ow.writeValueAsString(q2);
    }

    @Test
    void askQuestion() throws Exception {
        String qid1string = this.mockMvc
                .perform(post("localhost:8080/api/question/ask")
                        .contentType(APPLICATION_JSON)
                        .content(q1Json)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long qid1 = Long.parseLong(qid1string);
        assertEquals(1L, qid1);
    }

    @Test
    void fetchQuestions() throws Exception {
        String qid1string = this.mockMvc
                .perform(post("localhost:8080/api/question/ask").contentType(APPLICATION_JSON)
                .content(q1Json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long qid1 = Long.parseLong(qid1string);

        String qid2string = this.mockMvc
                .perform(post("localhost:8080/api/question/ask").contentType(APPLICATION_JSON)
                .content(q2Json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long qid2 = Long.parseLong(qid2string);
        String listLecture1string = this.mockMvc
                .perform(get("localhost:8080/api/question/fetch?lid=" + lectureEntity1.getUuid().toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String listLecture2string = this.mockMvc
                .perform(get("localhost:8080/api/question/fetch?lid=" + lectureEntity2.getUuid().toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<QuestionEntity> listLecture1 = objectMapper.readValue(listLecture1string,  new TypeReference<List<QuestionEntity>>(){});
        List<QuestionEntity> listLecture2 = objectMapper.readValue(listLecture2string,  new TypeReference<List<QuestionEntity>>(){});
        assertEquals(1, listLecture1.size());
        assertEquals(2, listLecture1.size());
        assertEquals(qid1, listLecture1.get(0).getId());
        assertEquals(qid2, listLecture2.get(0).getId());



    }

    @Test
    void deleteQuestion() {
    }

    @Test
    void modDelete() {
    }

    @Test
    void vote() {
    }
}