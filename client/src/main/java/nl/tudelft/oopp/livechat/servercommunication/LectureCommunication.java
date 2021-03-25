package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.controllers.AlertController;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static nl.tudelft.oopp.livechat.businesslogic.CommonCommunication.handleResponse;

/**
 * Class for Lecture server communication.
 */
public class LectureCommunication {

    private LectureCommunication() {

    }

    /**
     * Client object for sending requests.
     */
    private static final HttpClient client = HttpClient.newBuilder().build();

    private static final String ADDRESS = CommonCommunication.ADDRESS;

    /**
     * Gson object for parsing Json
     * set to parse fields according to annotations
     * and with specified date format.
     */
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

    /**
     * Creates a new lecture.
     * @param name A name of the lecture
     * @param creatorName the creator name
     * @param startTime the start time
     * @return Lecture which was created, null in case of errors
     */
    public static Lecture createLecture(String name, String creatorName,
                                        Timestamp startTime, int frequency) {

        //Encoding the lecture name into url compatible format
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);

        //Creating node
        JsonObject jsonObject = new JsonObject();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String start = date.format(startTime);
        jsonObject.addProperty("creatorName", creatorName);
        jsonObject.addProperty("startTime", start);
        jsonObject.addProperty("frequency", frequency);

        //Convert node to string
        String nodeToString = gson.toJson(jsonObject);

        //Parameters for request
        HttpRequest.BodyPublisher req = HttpRequest.BodyPublishers.ofString(nodeToString);
        String address = ADDRESS + "/api/newLecture?name=";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().POST(req)
                .uri(URI.create(address + name)).build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        int result = handleResponse(response);
        if (result != 0) {
            return null;
        }

        //Return object from response
        Lecture created =  gson.fromJson(response.body(), Lecture.class);

        if (!registerUser(created.getUuid().toString(), User.getUid(), User.getUserName())) {
            System.out.println("Couldn't register user");
            return null;
        }
        return created;

    }

    /**
     * Sends an HTTP request to get a lecture by its uuid.
     * @param lectureId The uuid of the lecture
     * @return Lecture object if the lecture exists on server, or null if it doesn't
     */
    public static Lecture joinLectureById(String lectureId) {
        //Encoding the lecture id into url compatible format
        lectureId = URLEncoder.encode(lectureId, StandardCharsets.UTF_8);

        //Parameter for request
        String address = ADDRESS + "/api/get/";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create(address + lectureId)).build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int result = handleResponse(response);
        if (result != 0) {
            return null;
        }

        final String lectureReceived = response.body();

        if (!registerUser(lectureId, User.getUid(), User.getUserName())) {
            System.out.println("Couldn't register user");
            return null;
        }

        //Return object from response
        return gson.fromJson(lectureReceived, Lecture.class);
    }

    /**
     * Validate moderator.
     * @param lectureId the lecture id
     * @param modKey    the moderator key
     * @return true if the moderator has been validated successfully,         false otherwise
     */
    public static boolean validateModerator(String lectureId, String modKey) {
        //Encoding the lecture id and modKey into url compatible format
        lectureId = URLEncoder.encode(lectureId, StandardCharsets.UTF_8);
        modKey = URLEncoder.encode(modKey, StandardCharsets.UTF_8);

        //Parameter for request
        String address = ADDRESS + "/api/validate/";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create(address + lectureId + "/" + modKey)).build();

        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        int result = handleResponse(response);
        return result == 0;

    }

    /**
     * Close lecture.
     * @param uuid the uuid
     * @param modkey the modkey
     * @return true if the lecture has been successfully closed by the server
     *          false otherwise
     */
    public static boolean closeLecture(String uuid, String modkey) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return false;
        }

        HttpRequest.BodyPublisher req = HttpRequest.BodyPublishers.ofString("");
        String address = ADDRESS + "/api/close/";

        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + uuid
                        + "/" + modkey)).build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        int result = handleResponse(response);
        return result == 0;
    }

    /**
     * Ban/unban the user by id or ip (done by moderator).
     * @param modKey the moderator key
     * @param questionToBanId the id of the question whose user is to be to (un)banned
     * @param time the time of the ban
     * @param byIp true iff needs to be (un)banned by ip, false if by id
     * @return  0 if the user was banned/unbanned successfully
     *         -1 if current lecture does not exist
     *         -2 if an exception occurred when communicating with the server
     *         -3 if unexpected response was received //TODO to be modified
     *         -4 if the user was not banned/unbanned successfully
     *         (e.g wrong mod id, wrong modkey etc.)
     */
    public static int ban(String modKey, long questionToBanId, int time, boolean byIp) {
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!!!");
            return -1;
        }

        //Create a json object with the data to be sent
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("modid", User.getUid());
        jsonObject.addProperty("modkey", modKey);
        jsonObject.addProperty("qid", questionToBanId);
        jsonObject.addProperty("time", time);
        String json = gson.toJson(jsonObject);

        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);
        String address = ADDRESS + "/api/user/ban/id";
        if (byIp) {
            address = ADDRESS + "/api/user/ban/ip";
        }

        //Create request and define response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address)).setHeader("Content-Type", "application/json").build();
        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Error when communicating with the server!");
            return -2;
        }
        return handleResponse(response);
    }

    /**
     * A helper method to register user on the server side.
     * @param lectureId the id of the lecture the user is in
     * @param uid the user id
     * @param username the user name
     * @return true if registered successfully, false otherwise
     */
    private static boolean registerUser(String lectureId, long uid, String username) {
        //request to add user to user table
        //Parameter for request
        String address = ADDRESS + "/api/user/register";

        JsonObject user = new JsonObject();
        user.addProperty("userName", username);
        user.addProperty("uid", uid);
        user.addProperty("lectureId", lectureId);

        String json = gson.toJson(user);
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);


        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().POST(req).uri(
                URI.create(address)).setHeader("Content-Type", "application/json").build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return handleResponse(response) == 0;
    }


    public static void registerUserdebug(String lectureId, long uid, String username) {
        registerUser(lectureId, uid, username);
    }


}
