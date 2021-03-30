package nl.tudelft.oopp.livechat.controllers;

import nl.tudelft.oopp.livechat.exceptions.InvalidModkeyException;
import nl.tudelft.oopp.livechat.exceptions.LectureException;
import nl.tudelft.oopp.livechat.exceptions.UserException;
import nl.tudelft.oopp.livechat.services.LectureSpeedService;
import nl.tudelft.oopp.livechat.services.PollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vote")
public class UserLectureVotingController {

    private final LectureSpeedService speedService;

    /**
     * Creates new LectureVotingController.
     * @param speedService the LectureSpeedService
     */
    public UserLectureVotingController(LectureSpeedService speedService) {
        this.speedService = speedService;
    }

    // TODO reconsider user authentication

    /**
     * PUT Endpoint to vote on the lecture speed.
     * @param uuid the id of the lecture
     * @param uid the id of the user
     * @param speed the indication of the speed
     * @return 0 if successful
     * @throws LectureException when the lecture is not found, is closed or the vote
     *           is incorrect (not "faster" or "slower")
     * @throws UserException when the user is not in the lecture
     */
    @PutMapping("/lectureSpeed")
    public int voteOnLectureSpeed(@RequestParam long uid,
                                  @RequestParam UUID uuid, @RequestBody String speed)
            throws LectureException, UserException {
        return speedService.setUserLectureSpeedVote(uid, uuid, speed);
    }

    /**
     * GET Endpoint to get the number of votes for the lecture speed.
     * @param uuid the id of the lecture
     * @return the list of votes for the lecture speed
     *          (first number is for faster, second for slower)
     * @throws LectureException when the lecture is not found
     */
    @GetMapping("/getLectureSpeed/{UUID}")
    public List<Integer> getVotes(@PathVariable("UUID") UUID uuid) throws LectureException {
        return speedService.getVotes(uuid);
    }

    /**
     * DELETE Endpoint to reset the voting for the lecture speed (done by a moderator).
     * @param uuid the id of the lecture
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    @DeleteMapping("/resetLectureSpeedVote/{UUID}/{modkey}")
    public int delete(@PathVariable("modkey") UUID modkey,
                      @PathVariable("UUID") UUID uuid)
            throws LectureException, InvalidModkeyException {
        return speedService.resetLectureSpeed(uuid, modkey);
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
}
