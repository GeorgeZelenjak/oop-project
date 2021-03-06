package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.services.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    ObjectMapper objectMapper = new ObjectMapper();

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

    /**.
     * PUT Endpoint to edit a specific question if you are a moderator.
     */
    @PutMapping("/edit")
    public int edit(@RequestBody String newQuestion) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(newQuestion);
        long id = jsonNode.get("id").asLong();
        String modkey = jsonNode.get("modkey").asText();
        String newText = jsonNode.get("text").asText();
        long uid = jsonNode.get("uid").asLong();
        return questionService.editQuestion(id, modkey, newText, uid);
    }
}
