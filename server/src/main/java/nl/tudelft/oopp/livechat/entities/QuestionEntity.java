package nl.tudelft.oopp.livechat.entities;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * The class that represents a question entity.
 */
@Entity(name = "question")
@Table(name = "questions")
@DynamicUpdate
@EnableTransactionManagement
public class QuestionEntity {

    @Id
    @Column(name = "id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "id")
    private long id  = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);

    @Column(name = "lectureId")
    private UUID lectureId;

    @Column(name = "time")
    private Timestamp time = new Timestamp(System.currentTimeMillis() / 1000 * 1000);

    @Column(name = "votes")
    private int votes;

    @Column(name = "text", length = 2000)
    private String text;

    @Column(name = "status")
    private String status = "new";

    @Column(name = "answered")
    private boolean answered;

    @Column(name = "answerText", length = 2000)
    private String answerText;

    @Column(name = "edited")
    private boolean edited;

    @Column(name = "answerTime")
    private Timestamp answerTime;

    @JsonIgnore
    @Column(name = "ownerId")
    private long ownerId;

    @JsonIgnore
    @Column(name = "editorId")
    private long editorId;

    @Column(name = "ownername")
    String ownerName;

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
        this.time = Objects.requireNonNullElseGet(time,
            () -> new Timestamp(System.currentTimeMillis() / 1000 * 1000));
        this.text = text;
        this.ownerId = ownerId;
    }

    /**
     * Static constructor to create a question entity.
     * @param lectureId the id of the lecture
     * @param text the text of the question
     * @param time the time the question was asked
     * @param ownerId the owner id
     * @return the question entity
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
     *
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
     * Decrements the vote count of the question by 1.
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
     * Gets the status of the question.
     * @return the status of the question (e.g new, editing etc.)
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the status of the question.
     * @param status the new status of the question (e.g new, editing etc.)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Checks whether the question is (un)answered.
     * @return true if the question is answered, false otherwise
     */
    public boolean isAnswered() {
        return this.answered;
    }

    /**
     * Sets the question as (un)answered.
     * @param answered true or false to indicate if the question is (un)answered
     */
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    /**
     * Checks whether the question has been edited.
     * @return true if the question has been edited, false otherwise
     */
    public boolean isEdited() {
        return this.edited;
    }

    /**
     * Sets the question as (un)edited.
     * @param edited true or false to indicate if the question has been edited or not
     */
    public void setEdited(boolean edited) {
        this.edited = edited;
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
     * Gets the owner name.
     * @return the owner name
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets owner name.
     * @param ownerName the owner name
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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
     * Gets editor id.
     * @return the editor id
     */
    public long getEditorId() {
        return editorId;
    }

    /**
     * Sets editor id.
     * @param editorId the editor id
     */
    public void setEditorId(long editorId) {
        this.editorId = editorId;
    }

    /**
     * Compares the question to another object.
     * @param o object to compare to
     * @return true iff the other object is also a QuestionEntity and has the same id.
     *          False otherwise
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
        return Objects.hash(this.id);
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
