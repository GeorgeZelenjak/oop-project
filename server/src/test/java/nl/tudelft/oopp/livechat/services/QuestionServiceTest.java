package nl.tudelft.oopp.livechat.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuestionServiceTest {
    LectureEntity l1;
    LectureEntity l2;
    QuestionEntity q1;
    QuestionEntity q2;
    QuestionEntity q3;

    @Autowired
    LectureService lectureService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    LectureRepository lectureRepository;


    @BeforeEach
    void setup() {
        l1 = lectureService.newLecture("Lecture Name 1", "Ivo van Kreveld");
        l2 = lectureService.newLecture("Lecture Name 2", "Stefan Hugtenburg");
        q1 = new QuestionEntity(l1.getUuid(), "name?",
                new Timestamp(System.currentTimeMillis()), 1L);
        q2 = new QuestionEntity(l2.getUuid(), "surname?",
                new Timestamp(System.currentTimeMillis()), 2L);
        q3 = new QuestionEntity(l1.getUuid(), "how old?",
                new Timestamp(System.currentTimeMillis()), 3L);
        questionService.newQuestionEntity(q1);
        questionService.newQuestionEntity(q2);
    }

    @Test
    @Order(1)
    void newQuestionEntityTest() {
        long result = questionService.newQuestionEntity(q3);
        assertTrue(result > 0);
    }

    @Test
    @Order(12)
    void newQuestionEntityUnsuccessfulTest() {
        long result = questionService.newQuestionEntity(q3);
        assertTrue(result > 0);
        q2.setId(q3.getId());
        long result2 = questionService.newQuestionEntity(q2);
        assertEquals(-1, result2);
        assertEquals(q3, questionRepository.findById(q2.getId()).get());
    }

    @Test
    @Order(2)
    void getQuestionsByLectureIdTest() {
        String lid = q1.getLectureId().toString();
        questionService.newQuestionEntity(q3);
        List<QuestionEntity> qs = questionService.getQuestionsByLectureId(UUID.fromString(lid));
        assertEquals(2, qs.size());
        if (qs.get(0).equals(q1)) {
            if (!qs.get(1).equals(q3)) fail();
        } else if (qs.get(0).equals(q3)) {
            if (!qs.get(1).equals(q1)) fail();
        } else fail();
    }

    @Test
    @Order(3)
    void deleteQuestionSuccessfulTest() {
        long pid = 1L;
        long qid = q1.getId();
        int result = questionService.deleteQuestion(qid, pid);
        assertEquals(result, 0);
    }

    @Test
    @Order(4)
    void deleteQuestionUnsuccessfulTest() {
        long pid = 2L;
        long qid = q1.getId();
        int result = questionService.deleteQuestion(qid, pid);
        assertNotEquals(result, 0);
    }

    @Test
    @Order(5)
    void deleteModeratorQuestionSuccessfulTest() {
        String modKey = l1.getModkey().toString();
        long qid = q1.getId();
        int result = questionService.deleteModeratorQuestion(qid, UUID.fromString(modKey));
        assertEquals(0, result);
    }

    @Test
    @Order(6)
    void deleteModeratorQuestionUnsuccessfulTest() {
        String modKey = l2.getModkey().toString();
        long qid = q1.getId();
        int result = questionService.deleteModeratorQuestion(qid, UUID.fromString(modKey));
        assertNotEquals(0, result);
    }

    @Test
    @Order(7)
    void editQuestionSuccessfulTest() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        String modKey = l1.getModkey().toString();
        String newText = "new text)))";
        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        q1 = questionRepository.findById(qid).orElse(null);
        assertEquals(0, result);
        assertEquals(newText, q1.getText());
    }

    @Test
    @Order(8)
    void editQuestionUnsuccessfulTest() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        String modKey = "l2.getModkey().toString()";
        String newText = "new text)))";
        assertThrows(IllegalArgumentException.class,
            () -> questionService.editQuestion(qid, modKey, newText, newOwnerId));
    }

    @Test
    @Order(9)
    void editQuestionUnsuccessful2Test() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        String modKey = l2.getModkey().toString();
        String newText = "new text)))";
        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        assertEquals(-1, result);
    }

    @Test
    @Order(10)
    void upvoteSuccessfulTest() {
        long qid = q1.getId();
        long uid = 27L;
        final int oldVotes = q1.getVotes();
        final int result = questionService.upvote(qid, uid);
        q1 = questionRepository.findById(qid).orElse(null);
        if (q1 == null) fail();
        assertEquals(0, result);
        assertEquals(oldVotes + 1, q1.getVotes());
    }

    @Test
    @Order(11)
    void upvoteUnsuccessfulTest() {
        long qid = q1.getId();
        long uid = 27L;
        final int oldVotes = q1.getVotes();
        questionService.upvote(qid, uid);
        final int result = questionService.upvote(qid, uid);
        q1 = questionRepository.findById(qid).orElse(null);
        if (q1 == null) fail();
        assertEquals(-1, result);
        assertEquals(oldVotes + 1, q1.getVotes());
    }
}