package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionServiceTest {
    LectureEntity l1;
    LectureEntity l2;
    QuestionEntity q1;
    QuestionEntity q2;
    QuestionEntity q3;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    LectureService lectureService;

    @Autowired
    LectureRepository lectureRepository;

    @BeforeEach
    void setup() {
       l1 = lectureService.newLecture("Lecture Name 1", "Professor X");
       l2 = lectureService.newLecture("Lecture Name 2", "Professor Y");
       q1 = new QuestionEntity(l1.getUuid(), "what's your name?", new Timestamp(System.currentTimeMillis()), 1);
       q2 = new QuestionEntity(l2.getUuid(), "what's your surname?", new Timestamp(System.currentTimeMillis()), 2);
       q3 = new QuestionEntity(l1.getUuid(), "how old are you?", new Timestamp(System.currentTimeMillis()), 3);
       questionService.newQuestionEntity(q1);
       questionService.newQuestionEntity(q2);
       questionService.newQuestionEntity(q3);
    }

    @Test
    void newQuestionEntity() {
        int result = questionService.newQuestionEntity(q1);
        assertEquals(0, result);
    }

    @Test
    void getQuestionsByLectureId() {
        QuestionEntity q = new QuestionEntity();

    }

    @Test
    void deleteQuestionSuccessful() {
    }

    @Test
    void deleteQuestionUnsuccessful() {
    }

    @Test
    void deleteModeratorQuestion() {
    }

    @Test
    void editQuestion() {
    }

    @Test
    void upvote() {
    }
}