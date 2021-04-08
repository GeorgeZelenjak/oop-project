package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.controllers.gui.AlertController;
import nl.tudelft.oopp.livechat.data.Poll;
import nl.tudelft.oopp.livechat.data.PollAndOptions;
import nl.tudelft.oopp.livechat.data.PollOption;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.*;

/**
 * Class for server communication related to polls.
 */
public abstract class PollCommunication {
    /**
     * Gson object for parsing Json set to parse fields according to annotations
     *     and with specified date format.
     */
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

    /**
     * The address of the server.
     */
    private static final String ADDRESS = CommonCommunication.ADDRESS;


    private PollCommunication() {

    }


    /**
     * Sends a request to create a new poll on the server.
     * @param lectureId the id of the lecture
     * @param modkey the moderator key
     * @param questionText the text of the question
     * @return the created Poll object if successful, null if not
     */
    public static Poll createPoll(UUID lectureId, UUID modkey, String questionText) {
        if (questionText == null || questionText.equals("")) {
            AlertController.alertError("Empty text", "Please enter the question text!");
            return null;
        }
        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(questionText);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI.create(ADDRESS
                + "/api/poll/create/" + URLEncoder.encode(lectureId.toString(),
                StandardCharsets.UTF_8) + "/" + URLEncoder.encode(modkey.toString(),
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponse(response) != 0) {
            return null;
        }
        return gson.fromJson(Objects.requireNonNull(response).body(), Poll.class);
    }


    /**
     * Sends a request to add an option for a poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @param isCorrect true if the option is correct, false if not
     * @param optionText the text of the option
     * @return the created poll option if successful, null if not
     */
    public static PollOption addOption(
            long pollId, UUID modkey, boolean isCorrect, String optionText) {
        if (optionText == null || optionText.equals("")) {
            AlertController.alertError("Empty text", "Please enter the option text!");
            return null;
        }
        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(optionText);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI.create(ADDRESS
                + "/api/poll/addOption/" + pollId + "/" + URLEncoder.encode(modkey.toString(),
                StandardCharsets.UTF_8) + "/" + isCorrect)).build();

        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponse(response) != 0) {
            return null;
        }
        return gson.fromJson(Objects.requireNonNull(response).body(), PollOption.class);
    }


    /**
     * Sends a request to toggle the state of the poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @return true if successful, false if not
     */
    public static boolean toggle(long pollId, UUID modkey) {
        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/poll/toggle/" + pollId + "/" + URLEncoder.encode(modkey.toString(),
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;
    }

    /**
     * Sends a request to fetch the latest poll and its options for a student
     *    (with hidden votes and correct answers).
     * @param lectureId the id of the lecture
     * @return the latest poll and its options if successful, null if not
     */
    public static PollAndOptions fetchPollAndOptionsStudent(UUID lectureId) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(ADDRESS
                + "/api/poll/fetchStudent/" + URLEncoder.encode(lectureId.toString(),
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponseNoAlerts(response) != 0) {
            return null;
        }
        return gson.fromJson(Objects.requireNonNull(response).body(), PollAndOptions.class);
    }

    /**
     * Sends a request to fetch the latest poll and its options for a lecturer.
     * @param lectureId the id of the lecture
     * @param modkey the moderator key
     * @return the latest poll and its options if successful, null if not
     */
    public static PollAndOptions fetchPollAndOptionsModerator(UUID lectureId, UUID modkey) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(ADDRESS
                + "/api/poll/fetchMod/" + URLEncoder.encode(lectureId.toString(),
                StandardCharsets.UTF_8) + "/" + URLEncoder.encode(modkey.toString(),
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);

        if (handleResponseNoAlerts(response) != 0) {
            return null;
        }
        return gson.fromJson(Objects.requireNonNull(response).body(), PollAndOptions.class);
    }

    /**
     * Sends a request to vote for a poll option.
     * @param userId the id of the user
     * @param pollOptionId the id of the poll option
     * @return true if successful, false if not
     */
    public static boolean vote(long userId, long pollOptionId) {
        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/poll/vote/" + userId + "/" + pollOptionId)).build();

        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;
    }

    /**
     * Sends a request to reset votes for a poll.
     * @param pollId the id of the poll
     * @param modkey the moderator key
     * @return true if successful, false if not
     */
    public static boolean resetVotes(long pollId, UUID modkey) {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/poll/reset/" + pollId + "/" + URLEncoder.encode(modkey.toString(),
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;
    }
}
