package nl.tudelft.oopp.livechat.controllers;

import java.util.List;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /*
    * This endpoint will be used to retrieve all the questions for the particular lecture
    */
    @GetMapping("/fetch")
    public List<QuestionEntity> fetchQuestions(@RequestParam String lid) {
        return questionService.getQuestionsByLectureId(lid);
    }

    /*
    * This endpoint will be used to ask a question
    */
    @PostMapping("/ask")
    public int askQuestion(@RequestBody QuestionEntity question) {
        return questionService.newQuestionEntity(question);
    }

    /*
    * This will be used to delete a question
     */
    @DeleteMapping("/delete")
    public int deleteQuestion(@RequestParam long qid, @RequestParam long uid) {
        return questionService.deleteQuestion(qid, uid);
    }

    /*
    * This endpoint is used when a moderator deletes any questions
     */
    @DeleteMapping("/moderator/delete")
    public int modDelete(@RequestParam long qid, @RequestParam String modkey) {
        return questionService.deleteModeratorQuestion(qid, modkey);
    }

    /*
    * This endpoint is used to upvote a specific question
     */
    @PostMapping("/upvote")
    public int vote(@RequestParam long id, @RequestParam long uid) {
        return questionService.upvote(id, uid);
    }
}
