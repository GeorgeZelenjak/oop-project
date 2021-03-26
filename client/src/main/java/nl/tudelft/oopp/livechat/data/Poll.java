package nl.tudelft.oopp.livechat.data;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Poll {

    private long id;

    private UUID lectureId;

    private String questionText = "";

    private Timestamp time;

    private long votes;

    private boolean open;



    /**
     * Instantiates a new Poll entity.
     */
    public Poll() {
    }

    /**
     * Instantiates a new Poll entity.
     *
     * @param lectureId    the lecture id
     * @param questionText the question text
     * @param time         the time
     * @param votes        the votes
     * @param isOpen       the is open
     */
    public Poll(UUID lectureId, String questionText, Timestamp time, long votes, boolean isOpen) {
        this.lectureId = lectureId;
        this.questionText = questionText;
        this.time = time;
        this.votes = votes;
        this.open = isOpen;
    }

    /**
     * Instantiates a new Poll entity.
     *
     * @param lectureId    the lecture id
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
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets lecture id.
     *
     * @return the lecture id
     */
    public UUID getLectureId() {
        return this.lectureId;
    }

    /**
     * Sets lecture id.
     *
     * @param lectureId the lecture id
     */
    public void setLectureId(UUID lectureId) {
        this.lectureId = lectureId;
    }

    /**
     * Gets question text.
     *
     * @return the question text
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Sets question text.
     *
     * @param questionText the question text
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Gets votes.
     *
     * @return the votes
     */
    public long getVotes() {
        return votes;
    }

    /**
     * Sets votes.
     *
     * @param votes the votes
     */
    public void setVotes(long votes) {
        this.votes = votes;
    }

    /**
     * Is open boolean.
     *
     * @return true if the poll is open, false otherwise
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Sets open.
     *
     * @param open true if poll is open, false if poll is closed
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll that = (Poll) o;
        //IT IS VERY IMPORTANT TO CHECK THE TEXT ON CLIENT
        return (id == that.id && questionText.equals(that.questionText));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, questionText);
    }
}
