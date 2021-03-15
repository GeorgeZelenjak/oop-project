package nl.tudelft.oopp.livechat.data;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Question class.
 */
public class Question implements Comparable<Question> {

    private static List<Question> currentQuestions;

    @Expose(serialize = false, deserialize = true)
    private long id;

    @Expose(serialize = true, deserialize = true)
    private UUID lectureId;

    @Expose(serialize = true, deserialize = true)
    private Timestamp time;

    @Expose(serialize = false, deserialize = true)
    private int votes;

    @Expose(serialize = true, deserialize = true)
    private String text;

    @Expose(serialize = false, deserialize = true)
    private boolean answered;

    @Expose(serialize = false, deserialize = true)
    private String answerText;

    @Expose(serialize = false, deserialize = true)
    private Timestamp answerTime;

    @Expose(serialize = true, deserialize = true)
    private long ownerId;

    @Expose(serialize = true, deserialize = true)
    private String ownerName;

    /**
     * Empty constructor to create a question entity.
     */
    public Question() {
    }

    /**
     * Instantiates a new Question entity.
     *
     * @param lectureId the lecture id
     * @param text      the text
     * @param ownerId   the owner id
     */
    public Question(UUID lectureId, String text, long ownerId) {
        this.lectureId = lectureId;
        this.text = text;
        this.ownerId = ownerId;
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
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Gets the time the question was asked.
     * @return the time the question was asked
     */
    public Timestamp getTime() {
        return this.time;
    }

    /**
     * Sets votes.
     *
     * @param votes the votes
     */
    public void setVotes(int votes) {
        this.votes = votes;
    }

    /**
     * Gets the number of votes.
     * @return the number of votes
     */
    public int getVotes() {
        return this.votes;
    }


    /**
     * Gets the text of the question.
     * @return the text of the question
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets answered.
     * @param answered boolean indicating if question is answered
     */
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    /**
     * Checks whether the question is (un)answered.
     * @return true if the question is answered, false otherwise
     */
    public boolean isAnswered() {
        return this.answered;
    }

    /**
     * Gets the text of the answer.
     * @return the text of the answer
     */
    public String getAnswerText() {
        return this.answerText;
    }

    /**
     * Gets the answer time of the question.
     * @return the answer time of the question if it is answered, null otherwise
     */
    public Timestamp getAnswerTime() {
        return this.answerTime;
    }

    /**
     * Gets the id of the owner of the question.
     * @return the id of the owner of the question.
     */
    public long getOwnerId() {
        return ownerId;
    }


    /**
     * Gets current questions.
     *
     * @return the current questions
     */
    public static List<Question> getCurrentQuestions() {
        return currentQuestions;
    }

    /**
     * Sets current questions.
     *
     * @param questions the questions
     */
    public static void setCurrentQuestions(List<Question> questions) {
        currentQuestions = questions;
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
        if (o instanceof Question) {
            Question q = (Question) o;
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

    /**
     * Compares the current question to the 'other' one.
     *
     * @param other the parameter
     * @return the result of the comparison
     */
    @Override
    public int compareTo(Question other) {
        return other.time.compareTo(this.time);
    }
}
