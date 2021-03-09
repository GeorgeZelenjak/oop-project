package nl.tudelft.oopp.livechat.communication;

import com.google.gson.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.QuestionEntity;

/**
 *  Class for communicating with server.
 */
public class ServerCommunication {

    //Client object for sending requests
    private static final HttpClient client = HttpClient.newBuilder().build();
    /* Gson object for parsing Json
    set to parse fields according to annotations
    and with specified date format
     */
    private static final Gson gson = new GsonBuilder().setDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz").excludeFieldsWithoutExposeAnnotation().create();


    /**
     * Creates a new lecture.
     * @param name      A name of the lecture
     * @return          Lecture which was created, null in case of errors
    */
    //  TODO: I AM PASSING A BLANK STRING IN THE POST METHOD, THIS SHOULD BE CHANGED
    public static Lecture createLecture(String name) {

        //Encoding the lecture name into url compatible format
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);

        //Parameters for request
        HttpRequest.BodyPublisher req = HttpRequest.BodyPublishers.ofString("");
        String address = "http://localhost:8080//api/newLecture?name=";

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
        }

        //Return object from response
        return gson.fromJson(response.body(), Lecture.class);

    }

    /**
     * Sends an HTTP request to get lecture by uuid.
     *
     * @param lectureId The uuid of the lecture
     * @return Lecture if the lecture exists on server or null if it doesn't
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
            System.out.println("Status: " + response.statusCode());
            return null;
        }

        //Return object from response
        return gson.fromJson(response.body(), Lecture.class);
    }

    /**
     * Sends an HTTP request to ask
     * a question with the current LectureID.
     *
     * @param questionText the question text
     * @return integer representing servers response
     *      or the lack of it
     *       1 - Question has been successfully asked
     *      -1 - Current lecture not set
     *      -2 - No response from server
     *      -3 - Unexpected response from server
     */
    public static int askQuestion(String questionText) {

        //Checking if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }

        //Parameters for question
        UUID lectureId = Lecture.getCurrentLecture().getUuid();
        QuestionEntity question = new QuestionEntity(lectureId,questionText,  42);

        //Parameters for request
        String json = gson.toJson(question);
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);
        String address = "http://localhost:8080/api/question/ask";
        String headerName = "Content-Type";
        String headerValue = "application/json";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().POST(req).uri(
                URI.create(address)).setHeader(headerName, headerValue).build();
        HttpResponse<String> response;

        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            e.printStackTrace();
            return -2;
        }

        //Unexpected response
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return -3;
        }

        //Question has been asked successfully
        System.out.println("The question was asked successfully! " + response.body());
        return 1;
    }



}
