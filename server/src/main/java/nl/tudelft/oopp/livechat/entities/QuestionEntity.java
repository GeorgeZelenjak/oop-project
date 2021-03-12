package nl.tudelft.oopp.livechat.entities;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.DynamicUpdate;


/**
 * The class that represents a question entity.
 */
@Entity(name = "question")
@Table(name = "questions")
@DynamicUpdate
public class QuestionEntity {

    @Id
    @Column(name = "id")
    private long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    @Column(name = "lectureId")
    private UUID lectureId;

    @Column(name = "time")
    private Timestamp time = new Timestamp(System.currentTimeMillis() % 10);

    @Column(name = "votes")
    private int votes;

    @Column(name = "text", length = 2000)
    private String text;

    @Column(name = "answered")
    private boolean answered;

    @Column(name = "answerText", length = 2000)
    private String answerText;

    @Column(name = "answerTime")
    private Timestamp answerTime;

    @JsonIgnore
    @Column(name = "ownerId")
    private long ownerId;

    /**
     * Empty constructor to create a question entity.
     */
    public QuestionEntity() {

    }

    /**
     * Constructor to create a question entity.
     * @param lectureId the id of the lecture
     * @param text the text of the question
     * @param time the time the question was asked
     * @param ownerId the owner id
     */
    public QuestionEntity(UUID lectureId, String text, Timestamp time, long ownerId) {
        this.lectureId = lectureId;
        this.time = time;
        this.text = text;
        this.ownerId = ownerId;
    }

    /**
     * Static constructor to create a question entity.
     * @param lectureId the id of the lecture
     * @param text the text of the question
     * @param time the time the question was asked
     * @param ownerId the owner id
     */
    public static QuestionEntity create(UUID lectureId, String text, Timestamp time, long ownerId) {
        QuestionEntity q = new QuestionEntity();
        q.lectureId = lectureId;
        q.time = time;
        q.text = text;
        q.ownerId = ownerId;
        return q;
    }

    /**
     * Sets id of the question.
     * @param id the new id of the question
     */
    @SuppressWarnings("unused")
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets id of the question.
     * @return the id of the question
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets the id of the lecture.
     * @return the id of the lecture
     */
    public UUID getLectureId() {
        return this.lectureId;
    }

    /**
     * Sets the id of the lecture.
     * @param lectureId the new id of the lecture
     */
    @SuppressWarnings("unused")
    public void setLectureId(UUID lectureId) {
        this.lectureId = lectureId;
    }

    /**
     * Gets the time the question was asked.
     * @return the time the question was asked
     */
    public Timestamp getTime() {
        return this.time;
    }

    /**
     * Gets the number of votes.
     * @return the number of votes
     */
    public int getVotes() {
        return this.votes;
    }

    /**
     * Increments the vote count of the question by 1.
     */
    public void vote() {
        this.votes++;
    }


    /**
     * Decrement question votes by 1.
     */
    public void unvote() {
        this.votes--;
    }

    /**
     * Gets the text of the question.
     * @return the text of the question
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text of the question.
     * @param text the new text of the question
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Checks whether the question is (un)answered.
     * @return true if the question is answered, false otherwise
     */
    public boolean isAnswered() {
        return this.answered;
    }

    /**
     * Sets the question an (un)answered.
     * @param answered true or false to indicate if the question is (un)answered
     */
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    /**
     * Gets the text of the answer.
     * @return the text of the answer
     */
    public String getAnswerText() {
        return this.answerText;
    }

    /**
     * Sets the answer text of the question.
     * @param answerText the answer text of the question
     */
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    /**
     * Gets the answer time of the question.
     * @return the answer time of the question if it is answered, null otherwise
     */
    public Timestamp getAnswerTime() {
        return this.answerTime;
    }

    /**
     * Sets the answer time of the question.
     * @param answerTime the answer time of the question
     */
    public void setAnswerTime(Timestamp answerTime) {
        this.answerTime = answerTime;
    }

    /**
     * Gets the id of the owner of the question.
     * @return the id of the owner of the question.
     */
    @JsonIgnore
    public long getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the id of the new owner of the question.
     * @param ownerId the id of the new owner of the question
     */
    @JsonProperty
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Compares the question to another object.
     * @param o object to compare to
     * @return true iff the other object is also a Question and has the same id. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof QuestionEntity) {
            QuestionEntity q = (QuestionEntity) o;
            return this.id == q.id;
        }
        return false;
    }

    /**
     * The hash code of the Question object.
     * @return the hash code of the Question object
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.lectureId, this.time);
    }

    /**
     * Converts the question object to String format.
     * @return the question object in String format
     */
    @Override
    public String toString() {
        return "QuestionEntity{"
                + "id=" + id
                + ", lectureId=" + lectureId
                + ", time=" + time
                + ", votes=" + votes
                + ", text='" + text + '\''
                + ", answered=" + answered
                + ", answerText='" + answerText + '\''
                + ", answerTime=" + answerTime
                + ", ownerId=" + ownerId
                + '}';
    }
}
