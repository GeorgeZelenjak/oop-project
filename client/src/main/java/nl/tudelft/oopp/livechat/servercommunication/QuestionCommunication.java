package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.Question;
import nl.tudelft.oopp.livechat.data.User;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.*;


public abstract class QuestionCommunication {

    private QuestionCommunication() {

    }

    /**
     * Gson object for parsing Json set to parse fields according to annotations
     *     and with specified date format.
     */
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

    /**
     * The address of the server.
     */
    private static final String ADDRESS = CommonCommunication.ADDRESS;

    /**
     * Sends an HTTP request to ask a question.
     * @param uid the id of the user
     * @param lectureId the id of the lecture
     * @param questionText the text of the question
     * @return 0 if the question has been asked successfully, -1 if not
     */
    public static boolean askQuestion(long uid, UUID lectureId, String questionText) {
        if (Lecture.getCurrent() == null) {
            return false;
        }

        String json = gson.toJson(new Question(lectureId, questionText, uid));
        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI.create(ADDRESS
                + "/api/question/ask")).setHeader("Content-Type",
                "application/json").build();

        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponse(response) != 0) {
            return false;
        }
        Objects.requireNonNull(response).body();
        User.addQuestionId(Long.parseLong(response.body()));
        return true;
    }

    /**
     * Sends an HTTP request to fetch questions that have been asked in current lecture.
     * @return the list of questions related to current lecture,
     *         null if error occurs or the user is not in the lecture
     */
    public static List<Question> fetchQuestions(boolean firstTime) {
        if (Lecture.getCurrent() == null) {
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(ADDRESS
                + "/api/question/fetch?lid=" + URLEncoder.encode(Lecture.getCurrent()
                        .getUuid().toString(),
                StandardCharsets.UTF_8) + "&firstTime=" + firstTime)).build();
        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponseNoAlerts(response) != 0) {
            return null;
        }
        Objects.requireNonNull(response).body();
        return gson.fromJson(response.body(), new TypeToken<List<Question>>(){}.getType());
    }

    /** Sends an HTTP request to upvote a question.
     * @param qid the id of the question
     * @param uid the id of the user
     * @return true if the question has been upvoted successfully, false if not
     */
    public static boolean upvoteQuestion(long qid, long uid) {
        if (Lecture.getCurrent() == null) {
            return false;
        }

        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/question/upvote" + "?qid=" + qid + "&uid=" + uid)).build();

        HttpResponse<String> response = sendAndReceive(request);

        int result = handleResponse(response);
        if (result == 0) {
            Objects.requireNonNull(response).body();
        }
        return result == 0;
    }

    /**
     * Sends an HTTP request to mark a question as answered, possibly with the answer text.
     * @param qid the id of the question
     * @param modkey the moderator key
     * @return true if the question has been marked as answered successfully, false if not
     */
    public static boolean markedAsAnswered(long qid, UUID modkey, String answer) {
        if (Lecture.getCurrent() == null) {
            return false;
        }
        answer = answer == null ? " " : answer;
        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(answer);
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/question/answer/" + qid + "/" + URLEncoder.encode(modkey.toString(),
                StandardCharsets.UTF_8))).setHeader("Content-Type",
                "application/json").build();

        HttpResponse<String> response = sendAndReceive(request);
        int result = handleResponse(response);
        if (result == 0) {
            Objects.requireNonNull(response).body();
        }
        return result == 0;
    }

    /**
     * Sends an HTTP request to edit a question.
     * @param qid the id of the question
     * @param modKey the moderator key
     * @param newText the edited text of the question
     * @return true if the question has been edited successfully, false if not
     */
    public static boolean edit(long qid, UUID modKey, String newText) {
        if (Lecture.getCurrent() == null) {
            return false;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", qid);
        jsonObject.addProperty("modkey", modKey.toString());
        jsonObject.addProperty("text", newText);
        jsonObject.addProperty("uid", User.getUid());
        String json = gson.toJson(jsonObject);

        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(
                URI.create(ADDRESS + "/api/question/edit")).setHeader("Content-Type",
                "application/json").build();

        HttpResponse<String> response = sendAndReceive(request);

        int result = handleResponse(response);
        return result == 0;
    }

    /**
     * Sends an HTTP request to delete a question (done by the user who asked the question).
     * @param qid the id of the question
     * @param uid the id of the user
     * @return  true if the question has been deleted successfully, false if not
     */
    public static boolean deleteQuestion(long qid, long uid) {
        if (Lecture.getCurrent() == null) {
            return false;
        }

        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(ADDRESS
                + "/api/question/delete" + "?qid=" + qid + "&uid=" + uid)).build();

        HttpResponse<String> response = sendAndReceive(request);
        int result = handleResponse(response);
        if (result == 0) {
            User.deleteQuestionId(qid);
        }
        return result == 0;
    }

    /**
     * Sends an HTTP request to delete a question (done by the moderator).
     * @param qid the id of the question
     * @param modkey the moderator key
     * @return true if the question has been deleted successfully, false if not
     */
    //TODO remove qid from user's set of questions after deletion
    public static boolean modDelete(long qid, UUID modkey) {
        if (Lecture.getCurrent() == null) {
            return false;
        }

        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(ADDRESS
                + "/api/question/moderator/delete" + "?qid=" + qid + "&modkey="
                + URLEncoder.encode(modkey.toString(), StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        int result = handleResponse(response);

        return result == 0;
    }

    /**
     * Sends an HTTP request to set the status of a question (answering, editing, new etc.).
     * @param qid the id of the question
     * @param modkey the moderator key
     * @param status the status of the question
     * @param uid the id of the user
     * @return true if the status of the question has been set successfully, false if not
     */
    public static boolean setStatus(long qid, UUID modkey, String status, long uid) {
        if (Lecture.getCurrent() == null) {
            return false;
        }
        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(status);
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/question/status/" + qid + "/" + uid + "/"
                + URLEncoder.encode(modkey.toString(), StandardCharsets.UTF_8)))
                .setHeader("Content-Type", "application/json").build();

        HttpResponse<String> response = sendAndReceive(request);
        int result = handleResponseNoAlerts(response);

        return result == 0;
    }
}