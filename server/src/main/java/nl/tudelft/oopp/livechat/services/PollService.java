package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.poll.PollAndOptions;
import nl.tudelft.oopp.livechat.entities.poll.PollEntity;
import nl.tudelft.oopp.livechat.entities.poll.PollOptionEntity;
import nl.tudelft.oopp.livechat.entities.poll.UserPollVoteTable;
import nl.tudelft.oopp.livechat.exceptions.*;
import nl.tudelft.oopp.livechat.repositories.*;
import org.apache.tomcat.jni.Poll;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class PollService {

    private final PollOptionRepository pollOptionRepository;

    private final PollRepository pollRepository;

    private final UserPollVoteRepository userPollVoteRepository;

    private final LectureRepository lectureRepository;

    private final UserRepository userRepository;

    private final LectureService lectureService;

    /**
     * Creates a new PollService object.
     * @param pollOptionRepository the pollOptionRepository
     * @param pollRepository the pollRepository
     * @param userPollVoteRepository the userPollVoteRepository
     * @param lectureRepository the lectureRepository
     * @param userRepository the userRepository
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
     * Creates a new poll.
     * @param lectureId the id of the lecture
     * @param modkey the moderator key
     * @param questionText the text of the question
     * @return the created poll entity if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public PollEntity createPoll(UUID lectureId, UUID modkey, String questionText)
            throws LectureException, InvalidModkeyException {
        lectureService.validateModerator(lectureId, modkey);
        PollEntity pollEntity = new PollEntity(lectureId, questionText);
        pollRepository.save(pollEntity);
        return pollEntity;
    }

    /**
     * Toggles a poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws PollException when the poll is not found
     */
    public int togglePoll(long pollId, UUID modkey)
            throws LectureException, InvalidModkeyException, PollException {
        PollEntity pollEntity = pollRepository.findById(pollId);
        if (pollEntity == null) throw new PollNotFoundException();
        if (lectureService.validateModerator(pollEntity.getLectureId(), modkey) != 0)
            throw new InvalidModkeyException();
        pollEntity.setOpen(!pollEntity.isOpen());
        pollRepository.save(pollEntity);
        return 0;
    }


    /**
     * Adds an answer option to the poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @param optionText the text of the option
     * @param isCorrect boolean indicating if the option is correct
     * @return the new poll answer option entity if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws PollException when the poll is not found
     */
    public PollOptionEntity addOption(long pollId, UUID modkey, String optionText,
            boolean isCorrect) throws LectureException,
                InvalidModkeyException, PollException {
        PollEntity pollEntity = pollRepository.findById(pollId);
        if (pollEntity == null) throw new PollNotFoundException();
        lectureService.validateModerator(pollEntity.getLectureId(), modkey);
        PollOptionEntity pollOptionEntity = new PollOptionEntity(pollId, optionText, 0, isCorrect);
        pollOptionRepository.save(pollOptionEntity);
        return pollOptionEntity;
    }

    /**
     * Votes on a poll.
     * @param userId the id of the user
     * @param pollOptionId the id of the poll answer option
     * @return 0 if successful
     * @throws UserNotRegisteredException when the user is not registered
     * @throws PollException when the poll is not found, is closed or is already voted
     */
    public int voteOnPoll(long userId, long pollOptionId)
            throws UserNotRegisteredException, PollException {
        //Check if user exists
        if (userRepository.getUserEntityByUid(userId) == null)
            throw new UserNotRegisteredException();

        //Check if poll option exists
        PollOptionEntity pollOptionEntity = pollOptionRepository.findById(pollOptionId);
        if (pollOptionEntity == null) throw new PollNotFoundException();

        //Check if poll is open
        if (!pollRepository.findById(pollOptionEntity.getPollId()).isOpen())
            throw new PollNotOpenException();

        //Check if user is in the lecture
        if (!userRepository.getUserEntityByUid(userId).getLectureId().equals(
                pollRepository.findById(pollOptionEntity.getPollId()).getLectureId()))
            throw new UserNotRegisteredException();

        //Check if user already voted
        List<UserPollVoteTable> listOfUserVotes = userPollVoteRepository.findAllByUserId(userId);
        for (UserPollVoteTable upvt : listOfUserVotes) {
            if (pollOptionRepository.findById(upvt.getOptionId()).getPollId()
                    == pollOptionRepository.findById(pollOptionId).getPollId()
            ) throw new PollAlreadyVotedException();
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
     * Fetch the poll and all its options (without moderator key).
     * @param lectureId the id of the lecture
     * @return the poll and all its options if successful
     * @throws LectureNotFoundException when the lecture is not found
     * @throws PollNotFoundException when the poll is not found
     */
    public PollAndOptions fetchPollAndOptionsStudent(UUID lectureId)
            throws LectureNotFoundException, PollNotFoundException {
        if (lectureRepository.findLectureEntityByUuid(lectureId) == null)
            throw new LectureNotFoundException();
        List<PollEntity> polls = pollRepository.findAllByLectureIdOrderByTimeDesc(lectureId);
        if (polls.size() == 0) throw new PollNotFoundException();
        PollEntity pollEntity = polls.get(0);
        List<PollOptionEntity> pollOptions = pollOptionRepository
                .findAllByPollId(pollEntity.getId());
        if (pollEntity.isOpen()) {
            for (PollOptionEntity option : pollOptions) {
                option.setCorrect(false);
                //-2 is just for fun
                option.setVotes(-2);
            }
        }
        return new PollAndOptions(pollEntity, pollOptions);
    }

    /**
     * Fetch the poll and all its options (without moderator key).
     * @param lectureId the id of the lecture
     * @param modkey the moderator key
     * @return the poll and all its options if successful
     * @throws LectureException when the lecture is not found
     * @throws PollNotFoundException when the poll is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     */
    public PollAndOptions fetchPollAndOptionsLecturer(UUID lectureId, UUID modkey)
            throws LectureException, PollNotFoundException, InvalidModkeyException {
        lectureService.validateModerator(lectureId, modkey);
        if (lectureRepository.findLectureEntityByUuid(lectureId) == null)
            throw new LectureNotFoundException();
        List<PollEntity> polls = pollRepository.findAllByLectureIdOrderByTimeDesc(lectureId);
        if (polls.size() == 0) throw new PollNotFoundException();
        PollEntity pollEntity = polls.get(0);
        List<PollOptionEntity> pollOptions = pollOptionRepository
                .findAllByPollId(pollEntity.getId());
        return new PollAndOptions(pollEntity, pollOptions);
    }


    /**
     * Reset votes for a poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @return 0 if successful
     * @throws LectureException when the lecture is not found
     * @throws InvalidModkeyException when the moderator key is incorrect
     * @throws PollNotFoundException when the poll is not found
     */
    public int resetVotes(long pollId, UUID modkey)
            throws LectureException, InvalidModkeyException, PollNotFoundException {
        PollEntity pollEntity = pollRepository.findById(pollId);

        if (pollEntity == null) throw new PollNotFoundException();

        lectureService.validateModerator(pollEntity.getLectureId(), modkey);

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










