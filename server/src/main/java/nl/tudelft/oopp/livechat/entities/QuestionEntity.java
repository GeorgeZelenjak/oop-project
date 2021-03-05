package nl.tudelft.oopp.livechat.entities;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;


/**
 * The type Question entity.
 */
@Entity(name = "question")
@Table(name = "questions")
@DynamicUpdate
public class QuestionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private long id;

    //  @ManyToOne(fetch = FetchType.LAZY, optional = false)
    //  @JoinColumn(name = "lectureId", nullable = false)
    //  @OnDelete(action = OnDeleteAction.CASCADE)

    @Column(name = "lectureId")
    private UUID lectureId;

    @Column(name = "time")
    private Timestamp time;

    @Column(name = "votes")
    private int votes;

    @Column(name = "text")
    private String text;

    @Column(name = "status")
    //new, edited, ?deleted?
    private String status = new String("new");

    @Column(name = "answered")
    private boolean answered;

    @Column(name = "answerText")
    private String answerText;

    @Column(name = "answerTime")
    private Timestamp answerTime;

    @Column(name = "ownerId")
    private long ownerId;

    public QuestionEntity() {

    }


    /**
     * Instantiates a new Question entity.
     *
     * @param lectureId    the lecture
     * @param text       the text
     * @param time       the time
     * @param ownerId    the owner id
     */
    public QuestionEntity(UUID lectureId, String text, Timestamp time, long ownerId) {
        this.lectureId = lectureId;
        this.time = time;
        this.text = text;
        this.ownerId = ownerId;
    }

    /**
     * Instantiates creates new Question entity.
     *
     * @param lectureId    the lecture
     * @param text       the text
     * @param time       the time
     * @param ownerId    the owner id
     */
    public static QuestionEntity create(UUID lectureId, String text, Timestamp time, long ownerId) {
        QuestionEntity q = new QuestionEntity();
        q.lectureId = lectureId;
        q.time = time;
        q.text = text;
        q.ownerId = ownerId;
        return q;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public UUID getLectureId() {
        return this.lectureId;
    }

    public void setLectureId(UUID lectureId) {
        this.lectureId = lectureId;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public int getVotes() {
        return this.votes;
    }

    public void vote() {
        this.votes += 1;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAnswered() {
        return this.answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Timestamp getAnswerTime() {
        return this.answerTime;
    }

    public void setAnswerTime(Timestamp answerTime) {
        this.answerTime = answerTime;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.lectureId, this.time);
    }

    @Override
    public String toString() {
        return "QuestionEntity{"
                + "id=" + id
                + ", lectureId=" + lectureId
                + ", time=" + time
                + ", votes=" + votes
                + ", text='" + text + '\''
                + ", status='" + status + '\''
                + ", answered=" + answered
                + ", answerText='" + answerText + '\''
                + ", answerTime=" + answerTime
                + ", ownerId=" + ownerId
                + '}';
    }
}
