package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.poll.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.entities.poll.UserPollVoteTable;
import nl.tudelft.oopp.livechat.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PollService {
    final PollOptionRepository pollOptionRepository;
    final PollRepository pollRepository;
    final UserPollVoteRepository userPollVoteRepository;
    final LectureRepository lectureRepository;
    final UserRepository userRepository;
    final LectureService lectureService;

    /**
     * Instantiates a new Poll service.
     *
     * @param pollOptionRepository   the poll option repository
     * @param pollRepository         the poll repository
     * @param userPollVoteRepository the user poll vote repository
     */
    public PollService(PollOptionRepository pollOptionRepository, PollRepository pollRepository,
                       UserPollVoteRepository userPollVoteRepository,
                       LectureRepository lectureRepository,
                       UserRepository userRepository) {
        this.pollOptionRepository = pollOptionRepository;
        this.pollRepository = pollRepository;
        this.userPollVoteRepository = userPollVoteRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
        this.lectureService = new LectureService(lectureRepository);
    }

    /**
     * Create poll entity.
     *
     * @param uuid         the lecture id
     * @param modkey       the modkey
     * @param questionText the question text
     * @return the poll entity
     */
    public PollEntity createPoll(UUID uuid, UUID modkey, String questionText) {
        if (lectureService.validateModerator(uuid, modkey) != 0) return null;
        PollEntity pollEntity = new PollEntity(uuid, questionText);
        pollRepository.save(pollEntity);
        return pollEntity;
    }

    /**
     * Toggle poll.
     *
     * @param pollId the poll id
     * @param modkey the modkey
     * @return 0 if the poll was toggled successfully
     *        -1 if poll does not exist
     *        -2 if invalid modkey
     */
    public int togglePoll(long pollId, UUID modkey) {
        PollEntity pollEntity = pollRepository.findById(pollId);
        if (pollEntity == null) return -1;
        if (lectureService.validateModerator(pollEntity.getUuid(), modkey) != 0) return -2;
        pollEntity.setOpen(!pollEntity.isOpen());
        pollRepository.save(pollEntity);
        return 0;
    }


    /**
     * Add option poll entity.
     *
     * @param pollId     the poll id
     * @param modkey     the modkey
     * @param optionText the option text
     * @param isCorrect  boolean indicating if the option is correct
     * @return the poll option entity
     */
    public PollOptionEntity addOption(long pollId, UUID modkey, String optionText,
                                      boolean isCorrect) {
        PollEntity pollEntity = pollRepository.findById(pollId);
        if (pollEntity == null
                || lectureService.validateModerator(pollEntity.getUuid(), modkey) != 0) return null;
        PollOptionEntity pollOptionEntity = new PollOptionEntity(pollId, optionText, 0, isCorrect);
        pollOptionRepository.save(pollOptionEntity);
        return pollOptionEntity;
    }

    /**
     * Vote on poll.
     *
     * @param userId       the user id
     * @param pollOptionId the poll option id
     * @return 0 if the poll was toggled successfully
     *        -1 if the user does not exist
     *        -2 if the poll option doesn't exist
     *        -3 if the poll is closed
     *        -4 if the user is not in the lecture
     *        -5 if the user already voted
     */
    public int voteOnPoll(long userId, long pollOptionId) {
        //Check if user exists
        if (userRepository.getUserEntityByUid(userId) == null) return -1;

        //Check if poll option exists
        PollOptionEntity pollOptionEntity = pollOptionRepository.findById(pollOptionId);
        if (pollOptionEntity == null) return -2;

        //Check if poll is open
        if (!pollRepository.findById(pollOptionEntity.getPollId()).isOpen()) return -3;

        //Check if user is in the lecture
        if (!userRepository.getUserEntityByUid(userId).getLectureId().equals(
                pollRepository.findById(pollOptionEntity.getPollId()).getUuid())) return -4;

        //Check if user already voted
        List<UserPollVoteTable> listOfUserVotes = userPollVoteRepository.findAllByUserId(userId);
        for (UserPollVoteTable upvt : listOfUserVotes) {
            if (pollOptionRepository.findById(upvt.getOptionId()).getPollId()
                    == pollOptionRepository.findById(pollOptionId).getPollId()
            ) return -5;
        }
        pollOptionEntity.setVotes(pollOptionEntity.getVotes() + 1);
        PollEntity poll = pollRepository.findById(pollOptionEntity.getPollId());
        poll.setVotes(poll.getVotes() + 1);
        pollRepository.save(poll);
        pollOptionRepository.save(pollOptionEntity);
        userPollVoteRepository.save(new UserPollVoteTable(userId, pollOptionId));
        return 0;
    }

    /**
     * Fetch poll and poll options.
     *
     * @param uuid the uuid
     * @return the poll and options
     */
    public PollAndOptions fetchPollAndOptions(UUID uuid) {
        if (lectureRepository.findLectureEntityByUuid(uuid) == null) return null;
        PollEntity pollEntity = pollRepository.findAllByUuidOrderByTimeDesc(uuid).get(0);
        if (pollEntity == null) return null;
        List<PollOptionEntity> pollOptions = pollOptionRepository
                .findAllByPollId(pollEntity.getId());
        return new PollAndOptions(pollEntity, pollOptions);
    }


    /**
     * Reset votes.
     *
     * @param pollId the poll id
     * @param modkey the modkey
     * @return 0 if the poll votes were resest successfully
     *        -1 if the poll does not exist
     *        -2 if the modkey is invalid
     *        -3 if the poll is closed
     *        -4 if the user is not in the lecture
     *        -5 if the user already voted
     */
    public int resetVotes(long pollId, UUID modkey) {
        PollEntity pollEntity = pollRepository.findById(pollId);
        if (pollEntity == null) return -1;
        if (lectureService.validateModerator(pollEntity.getUuid(), modkey) != 0) return -2;

        for (PollOptionEntity poe : pollOptionRepository.findAllByPollId(pollId)) {
            userPollVoteRepository.deleteAllByOptionId(poe.getId());
            poe.setVotes(0);
            pollOptionRepository.save(poe);
        }
        pollEntity.setVotes(0);
        pollRepository.save(pollEntity);
        return 0;
    }
}










