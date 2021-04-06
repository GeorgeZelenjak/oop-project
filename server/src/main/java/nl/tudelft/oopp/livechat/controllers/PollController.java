package nl.tudelft.oopp.livechat.controllers;

import nl.tudelft.oopp.livechat.entities.poll.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.services.PollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Class for the Poll controller.
 */
@RestController
@RequestMapping("/api/poll")
public class PollController {
    private final PollService pollService;

    /**
     * Instantiates a new Poll controller.
     *
     * @param pollService the poll service
     */
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    /**
     * POST endpoint to create a new poll.
     * @param lectureId    the lecture id
     * @param modkey       the moderator key
     * @param questionText the question text
     * @return the poll entity just created
     * @throws LectureException       when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    @PostMapping("/create/{lectureId}/{modkey}")
    public PollEntity createPoll(@PathVariable UUID lectureId, @PathVariable UUID modkey,
                                 @RequestBody String questionText)
            throws LectureException, InvalidModkeyException {
        return pollService.createPoll(lectureId, modkey, questionText);
    }

    /**
     * POST Endpoint to add an option to an existing poll.
     *
     * @param pollId     the poll id to which add the option
     * @param modkey     the moderator key
     * @param isCorrect  boolean indicating if it is a correct option
     * @param optionText the option text
     * @return the poll option entity just created
     * @throws LectureException       when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws PollException          when the poll is not found
     */
    @PostMapping("/addOption/{pollId}/{modkey}/{isCorrect}")
    public PollOptionEntity addOption(@PathVariable long pollId, @PathVariable UUID modkey,
                                      @PathVariable boolean isCorrect,
                                      @RequestBody String optionText)
            throws LectureException, InvalidModkeyException, PollException {
        return pollService.addOption(pollId, modkey, optionText, isCorrect);
    }

    /**
     * PUT endpoint to toggle a poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws PollException when the poll is not found
     */
    @PutMapping("/toggle/{pollId}/{modkey}")
    public int toggle(@PathVariable long pollId, @PathVariable UUID modkey)
            throws LectureException, InvalidModkeyException, PollException {
        return pollService.togglePoll(pollId, modkey);
    }

    /**
     * GET endpoint to fetch poll and options by students.
     *
     * @param lectureId the lecture id
     * @return the poll and options in a container class
     * @throws LectureNotFoundException when the lecture is not found
     * @throws PollNotFoundException when the poll is not found
     */
    @GetMapping("/fetchStudent/{lectureId}")
    public PollAndOptions fetchPollAndOptionsStudent(@PathVariable UUID lectureId)
            throws LectureNotFoundException, PollNotFoundException {
        return pollService.fetchPollAndOptionsStudent(lectureId);
    }

    /**
     * GET endpoint to fetch poll and options by moderators.
     *
     * @param uuid   the uuid of the lecture
     * @param modkey the moderator key for that lecture
     * @return the poll and options in a container class
     * @throws LectureException       when the lecture is not found
     * @throws PollNotFoundException  when the poll is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    @GetMapping("/fetchMod/{uuid}/{modkey}")
    public PollAndOptions fetchPollAndOptionsLecturer(
            @PathVariable UUID uuid, @PathVariable UUID modkey)
            throws LectureException, PollNotFoundException, InvalidModkeyException {
        return pollService.fetchPollAndOptionsLecturer(uuid, modkey);
    }

    /**
     * PUT Endpoint to vote on poll.
     *
     * @param userId       the user id
     * @param pollOptionId the poll option id
     * @return 0 if successful
     * @throws PollException when the poll is not found, is closed or is already voted
     * @throws UserException when the user is not registered
     */
    @PutMapping("/vote/{userId}/{pollOptionId}")
    public int voteOnPoll(@PathVariable long userId, @PathVariable long pollOptionId)
            throws PollException, UserException {
        return pollService.voteOnPoll(userId,pollOptionId);
    }

    /**
     * PUT Endpoint to reset votes for a poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws PollNotFoundException when the poll is not found
     */
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
}
