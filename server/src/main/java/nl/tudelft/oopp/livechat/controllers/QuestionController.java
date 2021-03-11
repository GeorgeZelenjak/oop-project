package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.services.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    ObjectMapper objectMapper = new ObjectMapper();

    private final QuestionService questionService;

    /**
     * Constructor for question controller.
     * @param questionService question service
     */
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * GET Endpoint to retrieve all the questions for the particular lecture.
     * @param lid id of the lecture
     * @return the list of questions associated with a particular lecture, or empty list
     */
    @GetMapping("/fetch")
    public List<QuestionEntity> fetchQuestions(@RequestParam UUID lid) {
        return questionService.getQuestionsByLectureId(lid);
    }

    /**
     * POST Endpoint to ask a question and store it in the database.
     * @param question question to be added to the database
     * @return the id assigned by the server for that question
     */
    @PostMapping("/ask")
    public long askQuestion(@RequestBody QuestionEntity question) {
        return questionService.newQuestionEntity(question);
    }

    /**
     * DELETE Endpoint to delete a question from the database (done by the author of the question).
     * @param qid the id of the question
     * @param uid the id of the user
     * @return 0 if successful, -1 otherwise
     */
    @DeleteMapping("/delete")
    public int deleteQuestion(@RequestParam long qid, @RequestParam long uid) {
        return questionService.deleteQuestion(qid, uid);
    }

    /**
     * DELETE Endpoint to delete any question from the database (done by a moderator).
     * @param qid the id of the question
     * @param modkey the moderator key
     * @return 0 if successful, -1 otherwise
     */
    @DeleteMapping("/moderator/delete")
    public int modDelete(@RequestParam long qid, @RequestParam UUID modkey) {
        return questionService.deleteModeratorQuestion(qid, modkey);
    }

    /**
     * PUT Endpoint to upvote a specific question.
     * @param qid the id of the question
     * @param uid the id of the user
     * @return 0 if successful, -1 otherwise
     */
    @PutMapping("/upvote")
    public int vote(@RequestParam long qid, @RequestParam long uid) {
        return questionService.upvote(qid, uid);
    }

    /**
     * PUT Endpoint to edit a specific question if you are a moderator.
     * @param newQuestion JSON with the id, text of the question and the moderator key
     * @return 0 if done, 400 if bad request, -1 otherwise (e.g unauthorized)
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

    /**
     * Exception handler.
     * @param exception exception that has occurred
     * @return response body with 404 and 'Invalid UUID' message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> badUUID(IllegalArgumentException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid UUID");
    }
}
