package nl.tudelft.oopp.livechat.data;

import java.sql.Timestamp;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Poll {

    private final long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    private UUID lectureId;

    private String questionText = "";

    private Timestamp time;

    private long votes;

    private boolean open;



    /**
     * Creates a new Poll object.
     */
    public Poll() {
    }

    /**
     * Creates a new Poll object with the specified parameters.
     * @param lectureId the id of the lecture
     * @param questionText the question text
     * @param time the time when the poll was created
     * @param votes the number of votes for the poll
     * @param isOpen is the poll open or not
     */
    public Poll(UUID lectureId, String questionText, Timestamp time, long votes, boolean isOpen) {
        this.lectureId = lectureId;
        this.questionText = questionText;
        this.time = time;
        this.votes = votes;
        this.open = isOpen;
    }

    /**
     * Creates a new Poll object with the specified parameters.
     * @param lectureId the id of the lecture
     * @param questionText the question text
     */
    public Poll(UUID lectureId, String questionText) {
        this.lectureId = lectureId;
        this.questionText = questionText;
        this.time = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        this.votes = 0;
        this.open = false;
    }

    /**
     * Gets the id of the poll.
     * @return the id of the poll
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the lecture id.
     * @return the lecture id
     */
    public UUID getLectureId() {
        return this.lectureId;
    }

    /**
     * Sets the lecture id.
     * @param lectureId the new lecture id
     */
    public void setLectureId(UUID lectureId) {
        this.lectureId = lectureId;
    }

    /**
     * Gets the question text.
     * @return the question text
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Sets question text.
     * @param questionText the question text
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * Gets the time when the poll was created.
     * @return the time when the poll was created
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Sets the time when the poll was created.
     * @param time the time when the poll was created.
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Gets the number of votes for the poll.
     * @return the number of votes for the poll
     */
    public long getVotes() {
        return votes;
    }

    /**
     * Sets the number of votes for the poll.
     * @param votes the number of votes for the poll
     */
    public void setVotes(long votes) {
        this.votes = votes;
    }

    /**
     * Checks if the is poll open or not.
     * @return true if the poll is open, false otherwise
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Sets the poll to be open or closed.
     * @param open true if the poll has to be open, false if the poll has to be closed
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * Compares the Poll object to another object.
     * @param o the other object to compare to
     * @return true iff the other object is also a Poll
     *         object and has the same id and the same text. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Poll) {
            Poll that = (Poll) o;
            //IT IS VERY IMPORTANT TO CHECK THE TEXT ON CLIENT
            //TODO why is that?
            return this.id == that.id;
        }
        return false;
    }

    /**
     * Generates the hash code for the Poll object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, questionText);
    }
}
