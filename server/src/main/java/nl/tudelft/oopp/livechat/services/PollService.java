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
     * @param lectureRepository      the lecture repository
     * @param userRepository         the user repository
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
     * @throws LectureException       the lecture exception
     * @throws InvalidModkeyException the invalid modkey exception
     */
    public PollEntity createPoll(UUID uuid, UUID modkey, String questionText)
            throws LectureException, InvalidModkeyException {
        lectureService.validateModerator(uuid, modkey);
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
     * @throws LectureException       the lecture exception
     * @throws InvalidModkeyException the invalid modkey exception
     * @throws PollException          the poll exception
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
     * Add option poll entity.
     *
     * @param pollId     the poll id
     * @param modkey     the modkey
     * @param optionText the option text
     * @param isCorrect  boolean indicating if the option is correct
     * @return the poll option entity
     * @throws LectureException       the lecture exception
     * @throws InvalidModkeyException the invalid modkey exception
     * @throws PollException          the poll exception
     */
    public PollOptionEntity addOption(long pollId, UUID modkey, String optionText,
                                      boolean isCorrect)
            throws LectureException, InvalidModkeyException, PollException {
        PollEntity pollEntity = pollRepository.findById(pollId);
        if (pollEntity == null) throw new PollNotFoundException();
        lectureService.validateModerator(pollEntity.getLectureId(), modkey);
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
     * @throws UserNotRegisteredException the user not registered exception
     * @throws PollNotFoundException      the poll not found exception
     * @throws PollNotOpenException       the poll not open exception
     * @throws PollAlreadyVotedException  the poll already voted exception
     */
    public int voteOnPoll(long userId, long pollOptionId) throws UserNotRegisteredException,
            PollNotFoundException, PollNotOpenException, PollAlreadyVotedException {
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
     * Fetch poll and poll options without modkey.
     *
     * @param uuid the uuid
     * @return the poll and options
     * @throws LectureNotFoundException the lecture not found exception
     * @throws PollNotFoundException    the poll not found exception
     */
    public PollAndOptions fetchPollAndOptionsStudent(UUID uuid)
            throws LectureNotFoundException, PollNotFoundException {
        if (lectureRepository.findLectureEntityByUuid(uuid) == null)
            throw new LectureNotFoundException();
        List<PollEntity> polls = pollRepository.findAllByLectureIdOrderByTimeDesc(uuid);
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
     * Fetch poll and poll options with modkey.
     *
     * @param uuid   the uuid
     * @param modkey the modkey
     * @return the poll and options
     * @throws LectureException       the lecture exception
     * @throws PollNotFoundException  the poll not found exception
     * @throws InvalidModkeyException the invalid modkey exception
     */
    public PollAndOptions fetchPollAndOptionsLecturer(UUID uuid, UUID modkey)
            throws LectureException, PollNotFoundException, InvalidModkeyException {
        lectureService.validateModerator(uuid, modkey);
        if (lectureRepository.findLectureEntityByUuid(uuid) == null)
            throw new LectureNotFoundException();
        List<PollEntity> polls = pollRepository.findAllByLectureIdOrderByTimeDesc(uuid);
        if (polls.size() == 0) throw new PollNotFoundException();
        PollEntity pollEntity = polls.get(0);
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
     * @throws LectureException       the lecture exception
     * @throws InvalidModkeyException the invalid modkey exception
     * @throws PollNotFoundException  the poll not found exception
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










