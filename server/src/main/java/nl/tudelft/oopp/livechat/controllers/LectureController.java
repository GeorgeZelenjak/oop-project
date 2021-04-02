package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.exceptions.InvalidModkeyException;
import nl.tudelft.oopp.livechat.exceptions.LectureException;
import nl.tudelft.oopp.livechat.services.LectureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;


/**
 * Class for the Lecture controller.
 */
@RestController
@RequestMapping("/api")
public class LectureController {

    private final LectureService service;

    /**
     * The Object mapper.
     */
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates new LectureController.
     * @param service the LectureService
     */
    public LectureController(LectureService service) {
        this.service = service;
    }


    /**
     * GET Endpoint to retrieve a lecture.
     * @param id the id of the lecture
     * @return the lecture object if successful
     * @throws LectureException when the lecture is not found or is not started yet
     */
    @GetMapping("/get/{id}")
    public LectureEntity getLecturesByID(@PathVariable("id") UUID id) throws LectureException {
        return service.getLectureByIdNoModkey(id);
    }


    /**
     * POST Endpoint to create a new lecture.
     * @param name the name of the lecture
     * @param info the object containing the creator name,
     *             the start time and the frequency of asking questions
     * @return a new lecture object if successful
     * @throws JsonProcessingException when invalid json is sent
     * @throws LectureException when the name is too long
     */
    @PostMapping("/newLecture")
    public LectureEntity newLecture(@RequestParam String name, @RequestBody String info)
            throws JsonProcessingException, LectureException {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z"));
        JsonNode jsonNode = objectMapper.readTree(info);
        String creatorName = jsonNode.get("creatorName").asText();
        Timestamp startTime = objectMapper.readValue(
                jsonNode.get("startTime").toString(),Timestamp.class);
        int frequency = Integer.parseInt(jsonNode.get("frequency").asText());
        return service.newLecture(name, creatorName, startTime, frequency);
    }

    /**
     * DELETE Endpoint to delete a lecture with the specified id iff the moderator key is correct.
     * @param modkey the moderator key to authenticate
     * @param id the id of the lecture
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    @DeleteMapping("/delete/{id}/{modkey}")
    public int delete(@PathVariable("modkey") UUID modkey, @PathVariable("id") UUID id)
            throws LectureException, InvalidModkeyException {
        return service.delete(id, modkey);
    }

    /**
     * PUT Endpoint to close a lecture with the specified id iff the moderator key is correct.
     * @param lectureId the id of the lecture
     * @param modkey the moderator key to authenticate
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    @PutMapping("/close/{lid}/{modkey}")
    public int close(@PathVariable("lid") UUID lectureId, @PathVariable("modkey") UUID modkey)
            throws LectureException, InvalidModkeyException {
        return service.close(lectureId, modkey);
    }

    /**
     * GET Endpoint to validate moderator key for the lecture.
     * @param modkey the moderator key to authenticate
     * @param id the id of the lecture
     * @return 0 if moderator was validated successfully
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    @GetMapping("/validate/{id}/{modkey}")
    public int validate(@PathVariable("modkey") UUID modkey, @PathVariable("id") UUID id)
            throws LectureException, InvalidModkeyException {
        return service.validateModerator(id, modkey);
    }

    /**
     * PUT Endpoint to set the frequency of asking questions of the lecture.
     * @param id the id of the lecture
     * @param modkey the moderator key
     * @param frequency the frequency of asking questions
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    @PutMapping("/validate/{id}/{modkey}")
    public int setFrequency(@PathVariable("id") UUID id, @PathVariable("modkey") UUID modkey,
                            @RequestParam int frequency) throws LectureException, InvalidModkeyException {
        return service.setFrequency(id, modkey, frequency);
    }

    /**
     * Exception handler for requests containing invalid uuids.
     * @param exception exception that has occurred
     * @return response object with 400 Bad Request status code and 'Don't do this' message
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

    /**
     * Exception handler.
     * @param exception exception that has occurred
     * @return response body with 400 and 'Not a number' message
     */
    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> badParameter(NumberFormatException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Not a number");
    }
}
