package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.oopp.livechat.entities.poll.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.services.PollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/poll")
public class PollController {
    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping("/create/{lectureId}/{modkey}")
    public PollEntity createPoll(@PathVariable UUID lectureId, @PathVariable UUID modkey,
                                 @RequestBody String questionText)
            throws LectureException, InvalidModkeyException {
        return pollService.createPoll(lectureId, modkey, questionText);
    }

    @PostMapping("/addOption/{pollId}/{modkey}/{isCorrect}")
    public PollOptionEntity addOption(@PathVariable long pollId, @PathVariable UUID modkey,
                                      @PathVariable boolean isCorrect,
                                      @RequestBody String optionText)
            throws LectureException, InvalidModkeyException, PollException {
        return pollService.addOption(pollId, modkey, optionText, isCorrect);
    }

    @PutMapping("/toggle/{pollId}/{modkey}")
    public int toggle(@PathVariable long pollId, @PathVariable UUID modkey)
            throws LectureException, InvalidModkeyException, PollException {
        return pollService.togglePoll(pollId, modkey);
    }

    @GetMapping("/fetchStudent/{lectureId}")
    public PollAndOptions fetchPollAndOptionsStudent(@PathVariable UUID lectureId)
            throws LectureNotFoundException, PollNotFoundException {
        return pollService.fetchPollAndOptionsStudent(lectureId);
    }

    @GetMapping("/fetchMod/{uuid}/{modkey}")
    public PollAndOptions fetchPollAndOptionsLecturer(
            @PathVariable UUID uuid, @PathVariable UUID modkey)
            throws LectureException, PollNotFoundException, InvalidModkeyException {
        return pollService.fetchPollAndOptionsLecturer(uuid, modkey);
    }

    @PutMapping("/vote/{userId}/{pollOptionId}")
    public int voteOnPoll(@PathVariable long userId, @PathVariable long pollOptionId)
            throws PollException, UserException {
        return pollService.voteOnPoll(userId,pollOptionId);
    }

    @PutMapping("/reset/{pollId}/{modkey}")
    public int resetVotes(@PathVariable long pollId, @PathVariable UUID modkey)
            throws LectureException, InvalidModkeyException, PollNotFoundException {
        return pollService.resetVotes(pollId,modkey);
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
