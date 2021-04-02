package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.handleResponse;
import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.sendAndReceive;

/**
 * Class for Lecture server communication.
 */
public abstract class LectureCommunication {

    private LectureCommunication() {

    }

    /**
     * The address of the server.
     */
    private static final String ADDRESS = CommonCommunication.ADDRESS;

    /**
     * Gson object for parsing Json according to annotations and with specified date format.
     */
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

    /**
     * Sends a request to create a new lecture.
     * @param name the name of the lecture
     * @param creatorName the name of the creator
     * @param startTime the start time of the lecture
     * @return the created lecture if successful, null if not
     */
    public static Lecture createLecture(String name, String creatorName,
                                        Timestamp startTime, int frequency) {
        JsonObject jsonObject = new JsonObject();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        jsonObject.addProperty("creatorName", creatorName);
        jsonObject.addProperty("startTime", date.format(startTime));
        jsonObject.addProperty("frequency", frequency);
        String nodeToString = gson.toJson(jsonObject);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(nodeToString);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI.create(ADDRESS
                + "/api/newLecture?name=" + URLEncoder.encode(name,
                StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponse(response) != 0) {
            return null;
        }
        Lecture created = gson.fromJson(Objects.requireNonNull(response).body(), Lecture.class);

        if (!registerUser(created.getUuid().toString())) {
            return null;
        }
        return created;
    }

    /**
     * Sends an HTTP request to get a lecture by its uuid.
     * @param lectureId the id of the lecture
     * @return the lecture if it exists on server, null if it doesn't
     */
    public static Lecture joinLectureById(String lectureId) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(ADDRESS
                + "/api/get/" + URLEncoder.encode(lectureId, StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponse(response) != 0) {
            return null;
        }
        boolean registered = registerUser(lectureId);
        if (!registered) {
            return null;
        }
        return gson.fromJson(Objects.requireNonNull(response).body(), Lecture.class);
    }

    /**
     * Sends an HTTP request to validate moderator given the moderator key.
     * @param lectureId the id of the lecture
     * @param modKey the moderator key
     * @return true if the moderator has been validated successfully, false otherwise
     */
    public static boolean validateModerator(String lectureId, String modKey) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(ADDRESS
                + "/api/validate/" + URLEncoder.encode(lectureId, StandardCharsets.UTF_8)
                + "/" + URLEncoder.encode(modKey, StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;

    }

    /**
     * Sends an HTTP request to close a lecture.
     * @param lectureId the id of the lecture
     * @param modkey the moderator key
     * @return true if the lecture has been closed by the server, false otherwise
     */
    public static boolean closeLecture(String lectureId, String modkey) {
        if (Lecture.getCurrent() == null) {
            return false;
        }
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/close/" + URLEncoder.encode(lectureId, StandardCharsets.UTF_8)
                + "/" + URLEncoder.encode(modkey, StandardCharsets.UTF_8))).build();

        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;
    }

    /**
     * Sends an HTTP request to ban a user by id or ip (done by moderator).
     * @param modKey the moderator key
     * @param questionToBanId the id of the question whose user is to be banned
     * @param time the time of the ban
     * @param byIp true if the user needs to be banned by ip, false if by id
     * @return true if the user was banned successfully, false otherwise
     */
    public static boolean ban(String modKey, long questionToBanId, int time, boolean byIp) {
        if (Lecture.getCurrent() == null) {
            System.out.println("You are not connected to a lecture!");
            return false;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("modid", User.getUid());
        jsonObject.addProperty("modkey", modKey);
        jsonObject.addProperty("qid", questionToBanId);
        jsonObject.addProperty("time", time);
        String json = gson.toJson(jsonObject);

        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(json);
        String address = byIp ? ADDRESS + "/api/user/ban/ip" : ADDRESS + "/api/user/ban/id";

        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(address))
                .setHeader("Content-Type", "application/json").build();
        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;
    }

    /**
     * Sends an HTTP request to ban a user by id or ip (done by moderator).
     * @param lectureId the id of the lecture
     * @param modkey the moderator key
     * @param frequency the frequency of asking questions
     * @return true if successful, false otherwise
     */
    public static boolean setFrequency(String lectureId, String modkey, int frequency) {
        if (Lecture.getCurrent() == null) {
            System.out.println("You are not connected to a lecture!");
            return false;
        }

        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().PUT(body).uri(URI.create(ADDRESS
                + "/api/frequency/" + URLEncoder.encode(lectureId, StandardCharsets.UTF_8)
                + "/" + URLEncoder.encode(modkey, StandardCharsets.UTF_8)
                + "?frequency=" + frequency)).build();

        HttpResponse<String> response = sendAndReceive(request);
        if (handleResponse(response) == 0) {
            Lecture.getCurrent().setFrequency(frequency);
        }
        return false;
    }

    /**
     * Sends an HTTP request to register user on the server.
     * @param lectureId the id of the lecture the user is in
     * @return true if registered successfully, false otherwise
     */
    public static boolean registerUser(String lectureId) {
        JsonObject user = new JsonObject();
        user.addProperty("userName", User.getUserName());
        user.addProperty("uid", User.getUid());
        user.addProperty("lectureId", lectureId);
        String json = gson.toJson(user);

        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body)
                .uri(URI.create(ADDRESS + "/api/user/register"))
                .setHeader("Content-Type", "application/json").build();
        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;
    }

    /**
     * A helper method for registering user in the debug mode.
     * @param lectureId the id of the lecture
     * @param uid the user id
     * @param username the username
     * @return true if successful, false otherwise
     */
    public static boolean registerUserdebug(String lectureId, long uid, String username) {
        JsonObject user = new JsonObject();
        user.addProperty("userName", username);
        user.addProperty("uid", uid);
        user.addProperty("lectureId", lectureId);
        String json = gson.toJson(user);

        HttpRequest.BodyPublisher body =  HttpRequest.BodyPublishers.ofString(json);
        String address = ADDRESS + "/api/user/register";
        HttpRequest request = HttpRequest.newBuilder().POST(body)
                .uri(URI.create(address))
                .setHeader("Content-Type", "application/json").build();

        HttpResponse<String> response = sendAndReceive(request);
        return handleResponse(response) == 0;
    }
}
