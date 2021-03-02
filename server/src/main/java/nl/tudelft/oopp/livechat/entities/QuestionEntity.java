package nl.tudelft.oopp.livechat.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


/**
 * The type Question entity.
 */
@Entity(name = "question")
@Table(name = "questions")
public class QuestionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "lectureId", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private UUID lectureId;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "votes")
    private int votes = 0;

    @Column(name = "text")
    private String text;

    //private String status

    @Column(name = "answered")
    private boolean answered = false;

    @Column(name = "answerText")
    private String answerText;

    @Column(name = "answerTime")
    private LocalDateTime answerTime;

    @Column(name = "ownerId")
    private long ownerId;

    public QuestionEntity() {

    }

    /**
     * Instantiates a new Question entity.
     *
     * @param lectureId    the lecture
     * @param text       the text
     * @param answerText the answer text
     * @param time       the time
     * @param ownerId    the owner id
     */
    public QuestionEntity(UUID lectureId, String text, String answerText,
                          LocalDateTime time, long ownerId) {
        this.lectureId = lectureId;
        this.time = time;
        this.votes = 0;
        this.text = text;
        this.answered = false;
        this.answerText = answerText;
        this.answerTime = null;
        this.ownerId = ownerId;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getLecture() {
        return lectureId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getVotes() {
        return votes;
    }

    public void vote() {
        ++this.votes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public LocalDateTime getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(LocalDateTime answerTime) {
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
}
