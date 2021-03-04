package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
//@TestInstance(Lifecycle.PER_CLASS)
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
        l2 = lectureService.newLecture("Lecture Name 2", "Professor Y");
        q1 = new QuestionEntity(l1.getUuid(), "name?", new Timestamp(System.currentTimeMillis()), 1L);
        q2 = new QuestionEntity(l2.getUuid(), "surname?", new Timestamp(System.currentTimeMillis()), 2L);
        q3 = new QuestionEntity(l1.getUuid(), "how old?", new Timestamp(System.currentTimeMillis()), 3L);
        questionService.newQuestionEntity(q1);
        questionService.newQuestionEntity(q2);
    }

    @Test
    @Order(1)
    void newQuestionEntityTest() {
        int result = questionService.newQuestionEntity(q3);
        assertEquals(0, result);
    }

    @Test
    @Order(2)
    void getQuestionsByLectureId() {
        String lid = q1.getLecture().toString();
        questionService.newQuestionEntity(q3);
        List<QuestionEntity> qs = questionService.getQuestionsByLectureId(lid);
        System.out.println(qs.size());
        assertTrue(qs.size() == 2 && qs.get(0).equals(q1) && qs.get(1).equals(q3));
    }

    @Test
    @Order(3)
    void deleteQuestionSuccessful() {
        long pid = 1L;
        long qid = q1.getId();
        int result = questionService.deleteQuestion(qid, pid);
        assertTrue(result == 0);
    }

    @Test
    @Order(4)
    void deleteQuestionUnsuccessful() {
        long pid = 2L;
        long qid = q1.getId();
        int result = questionService.deleteQuestion(qid, pid);
        assertFalse(result == 0);
    }

    @Test
    @Order(5)
    void deleteModeratorQuestionSuccessful() {
        String modKey = l1.getModkey().toString();
        long qid = q1.getId();
        int result = questionService.deleteModeratorQuestion(qid, modKey);
        assertEquals(0, result);
    }

    @Test
    @Order(6)
    void deleteModeratorQuestionUnsuccessful() {
        String modKey = l2.getModkey().toString();
        long qid = q1.getId();
        int result = questionService.deleteModeratorQuestion(qid, modKey);
        assertNotEquals(0, result);
    }

    @Test
    @Order(7)
    void editQuestionSuccessful() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        String modKey = l1.getModkey().toString();
        String newText = "new text)))";
        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        assertEquals(0, result);
        //assertEquals(newText, q1.getText());
    }

    @Test
    @Order(8)
    void editQuestionUnsuccessful() {
        long qid = q1.getId();
        long newOwnerId = 42L;
        String modKey = l2.getModkey().toString();
        String newText = "new text)))";
        int result = questionService.editQuestion(qid, modKey, newText, newOwnerId);
        assertNotEquals(0, result);
    }

    /*@Test
    @Order(9)
    void upvoteSuccessful() {
        //questionService.newQuestionEntity(q1);
        long qid = q1.getId();
        long uid = 27L;
        int oldVotes = q1.getVotes();
        q1.vote();
        int result = questionService.upvote(qid, uid);
        assertEquals(oldVotes + 1, q1.getVotes()); //0 == result
    }

    /*@Test
    @Order(10)
    void upvoteUnsuccessful() {
        long qid = q1.getId();
        long uid = 27L;
        int oldVotes = q1.getVotes();
        questionService.upvote(qid, uid);
        int result = questionService.upvote(qid, uid);
        assertTrue(-1 == result && q1.getVotes() == oldVotes + 1);
    }*/
}