package nl.tudelft.oopp.livechat.servercommunication;

//import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.User;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

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

    /**
     * Gson object for parsing Json
     * set to parse fields according to annotations
     * and with specified date format. // old - EEE, dd MMM yyyy HH:mm:ss zzz
     */
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-mm-dd hh:mm:ss").create();

    /**
     * Creates a new lecture.
     *
     * @param name        A name of the lecture
     * @param creatorName the creator name
     * @param startTime   the start time
     * @return Lecture which was created, null in case of errors
     */
    // I AM PASSING A BLANK STRING IN THE POST METHOD, THIS SHOULD BE CHANGED
    public static Lecture createLecture(String name, String creatorName, Timestamp startTime) {

        //Encoding the lecture name into url compatible format
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);

        //Creating node
        ObjectMapper mapper = new ObjectMapper();
        //mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        ObjectNode node = mapper.createObjectNode();
        node.put("creatorName", creatorName);
        node.put("startTime", String.valueOf(startTime));      //.getTime()

        //Convert node to string
        String nodeToString = node.toString();

        //Parameters for request
        HttpRequest.BodyPublisher req = HttpRequest.BodyPublishers.ofString(nodeToString);
        String address = "http://localhost:8080/api/newLecture?name=";

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

        // Prints the status code if the communication
        // if server gives an unexpected response
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return null;
        }

        //Return object from response
        return gson.fromJson(response.body(), Lecture.class);

    }

    /**
     * Sends an HTTP request to get a lecture by its uuid.
     *
     * @param lectureId The uuid of the lecture
     * @return Lecture object if the lecture exists on server, or null if it doesn't
     */
    public static Lecture joinLectureById(String lectureId) {
        //Encoding the lecture id into url compatible format
        lectureId = URLEncoder.encode(lectureId, StandardCharsets.UTF_8);

        //Parameter for request
        String address = "http://localhost:8080/api/get/";

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
        if (response.statusCode() != 200) {
            System.out.println("Status first req: " + response.statusCode());
            return null;
        }

        final String lectureReceived = response.body();

        //request to add user to user table
        //Parameter for request
        address = "http://localhost:8080/api/user/register";

        JsonObject user = new JsonObject();
        user.addProperty("userName", User.getUserName());
        user.addProperty("uid", User.getUid());
        user.addProperty("lectureId", lectureId);

        String json = gson.toJson(user);
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);

        //Creating request and defining response
        request = HttpRequest.newBuilder().POST(req).uri(
                URI.create(address)).setHeader("Content-Type", "application/json").build();

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status second req: " + response.statusCode());
            return null;
        }

        if (!response.body().equals("0")) {
            System.out.println("Server rejected the request");
            return null;
        }

        //Return object from response
        return gson.fromJson(lectureReceived, Lecture.class);
    }

    /**
     * Validate moderator.
     *
     * @param lectureId the lecture id
     * @param modKey    the moderator key
     * @return true if the moderator has been validated successfully,         false otherwise
     */
    public static boolean validateModerator(String lectureId, String modKey) {
        //Encoding the lecture id and modKey into url compatible format
        lectureId = URLEncoder.encode(lectureId, StandardCharsets.UTF_8);
        modKey = URLEncoder.encode(modKey, StandardCharsets.UTF_8);

        //Parameter for request
        String address = "http://localhost:8080/api/validate/";

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
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return false;
        }

        return response.body().equals("0");

    }

    /**
     * Close lecture.
     *
     * @param uuid   the uuid
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

        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create("http://localhost:8080/api/close/" + uuid
                        + "/" + modkey)).build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            System.out.println(response.body());
            return false;
        }

        return  response.body().equals("0");
    }

}
