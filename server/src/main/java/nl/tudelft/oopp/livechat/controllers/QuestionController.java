package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.services.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

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
     * @param lid the id of the lecture
     * @return the list of questions associated with a particular lecture, or empty list
     */
    @GetMapping("/fetch")
    public DeferredResult<List<QuestionEntity>> fetchQuestions(@RequestParam UUID lid,
                                                               @RequestParam boolean firstTime)
            throws LectureNotFoundException {
        if (!questionService.lectureExists(lid)) {
            throw new LectureNotFoundException();
        }
        long timeOutInMilliSec = 2 * 1000L;
        DeferredResult<List<QuestionEntity>> deferredResult =
                new DeferredResult<>(timeOutInMilliSec);
        CompletableFuture<Void> future;
        if (firstTime) {
            deferredResult.setResult(questionService.getQuestionsByLectureId(lid));
            return deferredResult;
        } else {
            future = CompletableFuture.runAsync(() -> {
                int count = 0;
                while (true) {
                    if (questionService.wasLectureChanged(lid)) {
                        deferredResult.setResult(questionService.getQuestionsByLectureId(lid));
                        break;
                    } else if (count >= 2000) {
                        break;
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count += 100;
                    }
                }
            }).orTimeout(2000, TimeUnit.MILLISECONDS);
        }
        deferredResult.onTimeout(() -> {
            future.cancel(true);
            deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("Request timeout occurred."));
            }
        );
        return deferredResult;
    }


    /**
     * POST Endpoint to ask a question and store it in the database.
     * @param question question to be added to the database
     * @return the id assigned by the server for that question
     * @throws UserException when the user is not registered or banned
     * @throws LectureException when the lecture is not found, is closed or is not started
     * @throws QuestionException when the question with the same id already exists, the question
     *           text is too long, or the user asks questions too frequently
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
     * @return 0 if successful
     * @throws UserException when the user is not registered
     * @throws LectureException when the lecture is not found or is closed
     * @throws QuestionException when the question is not found
     *          or the owner id doesn't match the provided id
     */
    @DeleteMapping("/delete")
    public int deleteQuestion(@RequestParam long qid, @RequestParam long uid)
            throws UserException, LectureException, QuestionException {
        return questionService.deleteQuestion(qid, uid);
    }

    /**
     * DELETE Endpoint to delete any question from the database (done by a moderator).
     * @param qid the id of the question
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws LectureException when the lecture is not found
     * @throws QuestionException when the question is not found
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
     * @return 0 if successful
     * @throws UserException when the user is not registered
     * @throws LectureException when the lecture is not found or is closed
     * @throws QuestionException when the question is not found
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
     * @throws JsonProcessingException when an invalid json is sent
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws QuestionException when the question is not found or the new question text is too long
     * @throws LectureException when the lecture is not found
     * @throws UserException when the new owner is not registered
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
     * @return 0 if successful
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws LectureException when the lecture is not found
     * @throws QuestionException when the question is not found or the answer text is too long
     */
    @PutMapping("/answer/{qid}/{modkey}")
    public int markAsAnswered(@PathVariable long qid, @PathVariable UUID modkey,
                              @RequestBody String answerText)
            throws InvalidModkeyException, LectureException, QuestionException {
        return questionService.answer(qid, modkey, answerText);
    }

    @PutMapping("/status/{qid}/{uid}/{modkey}")
    public int setStatus(@PathVariable long qid, @PathVariable UUID modkey,
                              @PathVariable long uid, @RequestBody String status)
            throws InvalidModkeyException, LectureException, QuestionException {
        return questionService.setStatus(status, qid, uid, modkey);
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
                .body("UUID is not in the correct format");
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
