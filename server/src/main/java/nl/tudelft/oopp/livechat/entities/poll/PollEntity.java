package nl.tudelft.oopp.livechat.entities.poll;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The type Poll entity.
 */
@Entity
@Table(name = "polls")
public class PollEntity {

    @Id
    @Column(name = "id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "id")
    private final long id  = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    @Column(name = "lectureId")
    private UUID lectureId;

    @Column(name = "questionText")
    private String questionText;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss Z")
    @Column(name = "time")
    private Timestamp time;

    @Column(name = "votes")
    private long votes;

    @Column(name = "isOpen")
    private boolean isOpen;

    /**
     * Creates a new Poll entity.
     */
    public PollEntity() {
    }

    /**
     * Creates a new Poll entity with the specified parameters.
     * @param lectureId the id of the lecture
     * @param questionText the question text
     * @param time the time when the poll was created
     * @param votes the number of votes for the poll
     * @param isOpen is the poll open or not
     */
    public PollEntity(UUID lectureId, String questionText,
                      Timestamp time, long votes, boolean isOpen) {
        this.lectureId = lectureId;
        this.questionText = questionText;
        this.time = time;
        this.votes = votes;
        this.isOpen = isOpen;
    }

    /**
     * Creates a new Poll entity with the specified parameters.
     * @param lectureId the id of the lecture
     * @param questionText the question text
     */
    public PollEntity(UUID lectureId, String questionText) {
        this.lectureId = lectureId;
        this.questionText = questionText;
        this.time = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        this.votes = 0;
        this.isOpen = false;
    }

    /**
     * Gets the id of the poll.
     * @return the id of the poll
     */
    public long getId() {
        return this.id;
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
        return this.questionText;
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
        return this.time;
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
        return this.votes;
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
        return this.isOpen;
    }

    /**
     * Sets the poll to be open or closed.
     * @param open true if the poll has to be open, false if the poll has to be closed
     */
    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    /**
     * Compares the PollEntity object to another object.
     * @param o the other object to compare to
     * @return true iff the other object is also a PollEntity
     *         object and has the same id. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PollEntity) {
            PollEntity that = (PollEntity) o;
            return this.id == that.id;
        }
        return false;
    }

    /**
     * Generates the hash code for the PollEntity object.
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
