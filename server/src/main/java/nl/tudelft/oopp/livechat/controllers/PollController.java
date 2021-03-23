package nl.tudelft.oopp.livechat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.oopp.livechat.entities.poll.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.services.PollService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/poll")
public class PollController {
    private final PollService pollService;

    ObjectMapper objectMapper = new ObjectMapper();

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PutMapping("/create/{uuid}/{modkey}")
    public PollEntity createPoll(@PathVariable UUID uuid, @PathVariable UUID modkey,
                                 @RequestBody String questionText) {
        return pollService.createPoll(uuid, modkey, questionText);
    }

    @PutMapping("/addOption/{pollId}/{modkey}/{isCorrect}")
    public PollOptionEntity addOption(@PathVariable long pollId, @PathVariable UUID modkey,
                                      @PathVariable boolean isCorrect,
                                      @RequestBody String optionText) {
        return pollService.addOption(pollId, modkey, optionText, isCorrect);
    }

    @PutMapping("/toggle/{pollId}/{modkey}")
    public int toggle(@PathVariable long pollId, @PathVariable UUID modkey) {
        return pollService.togglePoll(pollId, modkey);
    }

    @GetMapping("/fetch/{uuid}")
    public PollAndOptions fetchPollAndOptions(@PathVariable UUID uuid) {
        return pollService.fetchPollAndOptions(uuid);
    }

    @PutMapping("/vote/{userId}/{pollOptionId}")
    public int voteOnPoll(@PathVariable long userId, @PathVariable long pollOptionId) {
        return pollService.voteOnPoll(userId,pollOptionId);
    }

    @DeleteMapping("/reset/{pollId}/{modkey}")
    public int resetVotes(@PathVariable long pollId, @PathVariable UUID modkey) {
        return pollService.resetVotes(pollId,modkey);
    }


}
