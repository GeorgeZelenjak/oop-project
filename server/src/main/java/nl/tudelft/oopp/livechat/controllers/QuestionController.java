package nl.tudelft.oopp.livechat.controllers;

import java.util.List;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.services.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;

    /**.
     * Constructor for question controller.
     * @param questionService question service
     */
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**.
     * GET Endpoint to retrieve all the questions for the particular lecture.
     */
    @GetMapping("/fetch")
    public List<QuestionEntity> fetchQuestions(@RequestParam String lid) {
        return questionService.getQuestionsByLectureId(lid);
    }

    /**.
     * POST Endpoint to ask a question and store it in the database.
     */
    @PostMapping("/ask")
    public long askQuestion(@RequestBody QuestionEntity question) {
        return questionService.newQuestionEntity(question);
    }

    /**
     * DELETE Endpoint to delete a question from the database (done by the author of the question).
     */
    @DeleteMapping("/delete")
    public int deleteQuestion(@RequestParam long qid, @RequestParam long uid) {
        return questionService.deleteQuestion(qid, uid);
    }

    /**.
     * DELETE Endpoint to delete any question from the database (done by a moderator).
     */
    @DeleteMapping("/moderator/delete")
    public int modDelete(@RequestParam long qid, @RequestParam String modkey) {
        return questionService.deleteModeratorQuestion(qid, modkey);
    }

    /**.
     * PUT Endpoint to upvote a specific question.
     */
    @PutMapping("/upvote")
    public int vote(@RequestParam long qid, @RequestParam long uid) {
        return questionService.upvote(qid, uid);
    }
}
