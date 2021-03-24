package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.exceptions.InvalidModkeyException;
import nl.tudelft.oopp.livechat.exceptions.LectureException;
import nl.tudelft.oopp.livechat.exceptions.QuestionException;
import nl.tudelft.oopp.livechat.exceptions.UserException;
import nl.tudelft.oopp.livechat.services.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Class for the Question controller.
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    /**
     * The Object mapper.
     */
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
    public long askQuestion(@RequestBody QuestionEntity question)
            throws UserException, LectureException, QuestionException {
        return questionService.newQuestionEntity(question);
    }

    /**
     * DELETE Endpoint to delete a question from the database (done by the author of the question).
     * @param qid the id of the question
     * @param uid the id of the user
     * @return 0 if successful, -1 otherwise
     */
    @DeleteMapping("/delete")
    public int deleteQuestion(@RequestParam long qid, @RequestParam long uid)
            throws UserException, LectureException, QuestionException {
        return questionService.deleteQuestion(qid, uid);
    }

    /**
     * DELETE Endpoint to delete any question from the database (done by a moderator).
     * @param qid    the id of the question
     * @param modkey the moderator key
     * @return 0 if successful, -1 otherwise
     */
    @DeleteMapping("/moderator/delete")
    public int modDelete(@RequestParam long qid, @RequestParam UUID modkey)
            throws InvalidModkeyException, LectureException, QuestionException {
        return questionService.deleteModeratorQuestion(qid, modkey);
    }

    /**
     * PUT Endpoint to upvote a specific question.
     * @param qid the id of the question
     * @param uid the id of the user
     * @return 0 if successful, -1 otherwise
     */
    @PutMapping("/upvote")
    public int vote(@RequestParam long qid, @RequestParam long uid)
            throws UserException, LectureException, QuestionException {
        return questionService.upvote(qid, uid);
    }

    /**
     * PUT Endpoint to edit a specific question if you are a moderator.
     * @param newQuestion JSON with the id, text of the question and the moderator key
     * @return 0 if done, 400 if bad request, -1 otherwise (e.g unauthorized)
     * @throws JsonProcessingException the json processing exception
     */
    @PutMapping("/edit")
    public int edit(@RequestBody String newQuestion)
            throws JsonProcessingException, InvalidModkeyException,
                    QuestionException, LectureException, UserException {
        JsonNode jsonNode = objectMapper.readTree(newQuestion);
        long id = jsonNode.get("id").asLong();
        UUID modkey = UUID.fromString(jsonNode.get("modkey").asText());
        String newText = jsonNode.get("text").asText();
        long uid = jsonNode.get("uid").asLong();
        return questionService.editQuestion(id, modkey, newText, uid);
    }

    /**
     * PUT Endpoint to mark any question as answered.
     * @param qid the id of the question
     * @param modkey the moderator key
     * @param answerText the answer text
     * @return 0 if successful, -1 otherwise
     */
    @PutMapping("/answer/{qid}/{modkey}")
    public int markAsAnswered(@PathVariable long qid, @PathVariable UUID modkey,
                              @RequestBody String answerText)
            throws InvalidModkeyException, LectureException, QuestionException {
        return questionService.answer(qid, modkey, answerText);
    }

    /**
     * Exception handler for invalid uuids.
     * @param exception exception that has occurred
     * @return response body with 400 and 'Don't do this' message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> badUUID(IllegalArgumentException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Don't do this");
    }

    /**
     * Exception handler for invalid JSONs.
     * @param exception exception that has occurred
     * @return response body with 400 and 'Don't do this' message
     */
    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> invalidJSON(JsonProcessingException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Don't do this");
    }

    /**
     * Exception handler.
     * @param exception exception that has occurred
     * @return response body with 400 and 'Missing parameter' message
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> badParameter(NullPointerException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Missing parameter");
    }
}
