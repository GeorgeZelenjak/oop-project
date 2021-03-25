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
     * Constructor for the lecture controller.
     * @param service lecture service
     */
    public LectureController(LectureService service) {
        this.service = service;
    }


    /**
     * GET Endpoint to retrieve a lecture.
     * @param id the id
     * @return selected lecture
     */
    @GetMapping("/get/{id}")
    public LectureEntity getLecturesByID(@PathVariable("id") UUID id) throws LectureException {
        return service.getLectureByIdNoModkey(id);
    }


    /**
     * POST Endpoint to create a new lecture.
     * @param name the name of the lecture
     * @param info the info
     * @return a new lecture entity
     * @throws JsonProcessingException the json processing exception
     */
    @PostMapping("/newLecture")
    public LectureEntity newLecture(@RequestParam String name, @RequestBody String info)
            throws JsonProcessingException, LectureException {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z"));
        JsonNode jsonNode = objectMapper.readTree(info);
        String creatorName = jsonNode.get("creatorName").asText();
        Timestamp startTime = objectMapper.readValue(
                jsonNode.get("startTime").toString(),Timestamp.class);
        return service.newLecture(name, creatorName, startTime);
    }

    /**
     * DELETE Endpoint to delete a lecture with the specified id iff the moderator key is correct.
     * @param modkey the moderator key to authenticate
     * @param id     UUID of lecture
     * @return 0 if the lecture has been deleted successfully, -1 if not
     */
    @DeleteMapping("/delete/{id}/{modkey}")
    public int delete(@PathVariable("modkey") UUID modkey, @PathVariable("id") UUID id)
            throws LectureException, InvalidModkeyException {
        return service.delete(id, modkey);
    }

    /**
     * PUT Endpoint to close a lecture with the specified id iff the moderator key is correct.
     * @param lectureId UUID of lecture
     * @param modkey    the moderator key to authenticate
     * @return 0 if the lecture has been closed successfully, -1 if not
     */
    @PutMapping("/close/{lid}/{modkey}")
    public int close(@PathVariable("lid") UUID lectureId, @PathVariable("modkey") UUID modkey)
            throws LectureException, InvalidModkeyException {
        return service.close(lectureId, modkey);
    }

    /**
     * GET Endpoint to validate moderator key for the lecture.
     * @param modkey the moderator key to authenticate
     * @param id     UUID of lecture
     * @return 0 if moderator was validated successfully, -1 if not
     */
    @GetMapping("/validate/{id}/{modkey}")
    public int validate(@PathVariable("modkey") UUID modkey, @PathVariable("id") UUID id)
            throws LectureException, InvalidModkeyException {
        return service.validateModerator(id, modkey);
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
