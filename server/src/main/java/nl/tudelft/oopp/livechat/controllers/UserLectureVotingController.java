package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.oopp.livechat.entities.UserLectureSpeedTable;
import nl.tudelft.oopp.livechat.exceptions.InvalidModkeyException;
import nl.tudelft.oopp.livechat.exceptions.LectureException;
import nl.tudelft.oopp.livechat.exceptions.UserException;
import nl.tudelft.oopp.livechat.services.LectureSpeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vote")
public class UserLectureVotingController {

    private final LectureSpeedService service;

    /**
     * Constructor for lecture voting controller.
     * @param service lecture speed service
     */
    public UserLectureVotingController(LectureSpeedService service) {
        this.service = service;
    }

    // TODO reconsider user authentication
    /**
     * PUT Endpoint to vote on lecture speed.
     * @param uuid the id of the lecture
     * @param uid the id of the user
     * @param speed the type of the vote
     * @return 0 if successful, -1 otherwise
     */
    @PutMapping("/lectureSpeed")
    public int voteOnLectureSpeed(@RequestParam long uid,
                                  @RequestParam UUID uuid, @RequestBody String speed)
            throws LectureException, UserException {
        return service.setUserLectureSpeedVote(uid,uuid,speed);
    }

    @GetMapping("/getLectureSpeed/{UUID}")
    public List<Integer> getVotes(@PathVariable("UUID") UUID uuid) throws LectureException {
        return service.getVotes(uuid);
    }

    /**
     * DELETE Endpoint to delete any question from the database (done by a moderator).
     * @param uuid the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful, -1 otherwise
     */
    @DeleteMapping("/resetLectureSpeedVote/{UUID}/{modkey}")
    public int delete(@PathVariable("modkey") UUID modkey,
                      @PathVariable("UUID") UUID uuid)
            throws LectureException, InvalidModkeyException {
        return service.resetLectureSpeed(uuid, modkey);
    }

    /**
     * Exception handler.
     * @param exception exception that has occurred
     * @return response body with 400 and 'Invalid UUID' message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<Object> badUUID(IllegalArgumentException exception) {
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
