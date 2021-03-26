package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a new user controller.
     *
     * @param userService user service object
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST endpoint to create a new user in the database.
     *
     * @param user the new user
     * @return 0 if successful, -1 if not
     */
    @PostMapping("/register")
    public int newUser(@RequestBody UserEntity user) throws UserException, NoDataReceivedException {
        String remoteAddress = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        return userService.newUser(user, remoteAddress);
    }

    /**
     * PUT mapping to ban by id.
     *
     * @param info the information needed to ban
     * @return the result from service
     * @throws JsonProcessingException the json processing exception
     */
    @PutMapping("/ban/id")
    public int banById(@RequestBody String info) throws JsonProcessingException,
            UserException, LectureException, QuestionException, InvalidModkeyException {
        JsonNode jsonNode = objectMapper.readTree(info);
        long modid = Long.parseLong(jsonNode.get("modid").asText());
        long qid = Long.parseLong(jsonNode.get("qid").asText());
        UUID modkey = UUID.fromString(jsonNode.get("modkey").asText());
        int time = Integer.parseInt(jsonNode.get("time").asText());
        if (time < 0) {
            throw new NumberFormatException();
        }
        return userService.banById(modid, qid, modkey, time);
    }

    /**
     * PUT mapping to ban by id.
     *
     * @param info the information needed to ban
     * @return the result from service
     * @throws JsonProcessingException the json processing exception
     */
    @PutMapping("/ban/ip")
    public int banByIp(@RequestBody String info) throws JsonProcessingException,
            UserException, LectureException, QuestionException {
        JsonNode jsonNode = objectMapper.readTree(info);
        long modid = Long.parseLong(jsonNode.get("modid").asText());
        UUID modkey = UUID.fromString(jsonNode.get("modkey").asText());
        long qid = Long.parseLong(jsonNode.get("qid").asText());
        int time = Integer.parseInt(jsonNode.get("time").asText());
        if (time < 0) {
            throw new NumberFormatException();
        }
        return userService.banByIp(modid, qid, modkey, time);
    }

    /**
     * Exception handler for requests containing invalid uuids.
     * @param exception exception that has occurred
     * @return response object with 400 Bad Request status code and 'Don't do this' message
     */
    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> badUUID(InvalidFormatException exception) {
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
     * Exception handler for invalid numbers.
     * @param exception exception that has occurred
     * @return response body with 400 and 'Don't do this' message
     */
    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> invalidNumber(NumberFormatException exception) {
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
                .body("Don't do this");
    }
}
