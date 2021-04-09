package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.handleResponse;
import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.sendAndReceive;


public abstract class LectureSpeedCommunication {
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

    private LectureSpeedCommunication() {

    }

    /**
     * Sends a request to get votes on the lecture speed.
     * @param lectureId the id of the lecture
     * @return list of votes on the lecture speed (first entry is the faster count,
     *          second is the slower count) if successful, null if not
     *
     */
    public static List<Integer> getVotesOnLectureSpeed(UUID lectureId) {
        if (Lecture.getCurrent() == null) {
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(ADDRESS
                + "/api/vote/getLectureSpeed/" + URLEncoder.encode(lectureId.toString(),
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        int result = handleResponse(response);
        if (result != 0) {
            return null;
        }

        Objects.requireNonNull(response).body();
        return gson.fromJson(response.body(), new TypeToken<List<Integer>>(){}.getType());
    }

    /**
     * Sends a request to reset the votes for the lecture speed.
     * @param lectureId the id of the lecture
     * @param modkey the moderator key
     * @return true if the lecture speed votes have been reset successfully, false if not
     */
    public static boolean resetLectureSpeed(UUID lectureId, UUID modkey) {
        if (Lecture.getCurrent() == null) {
            return false;
        }

        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(ADDRESS
                + "/api/vote/resetLectureSpeedVote/" + URLEncoder.encode(lectureId.toString(),
                StandardCharsets.UTF_8) + "/" + URLEncoder.encode(modkey.toString(),
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        int result = handleResponse(response);

        return result == 0;
    }


    /**
     * Sends a request to vote on the lecture speed.
     * @param uid the id of the user
     * @param lectureId the id of the lecture
     * @param speed the speed preference
     * @return true if the vote has been recorded successfully, false if not
     */
    public static boolean voteOnLectureSpeed(long uid, UUID lectureId, String speed) {
        if (Lecture.getCurrent() == null) {
            return false;
        }

        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(speed);
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/vote/lectureSpeed" + "?uid=" + uid + "&uuid="
                + URLEncoder.encode(lectureId.toString(), StandardCharsets.UTF_8)))
                .setHeader("Content-Type", "application/json").build();

        HttpResponse<String> response = sendAndReceive(request);
        int result = handleResponse(response);

        return result == 0;
    }
}
