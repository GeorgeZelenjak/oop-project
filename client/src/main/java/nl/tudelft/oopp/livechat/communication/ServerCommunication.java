package nl.tudelft.oopp.livechat.communication;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import nl.tudelft.oopp.livechat.data.Lecture;
import nl.tudelft.oopp.livechat.data.QuestionEntity;

/**
 *  Class for communicating with server.
 */
public class ServerCommunication {

    private static HttpClient client = HttpClient.newBuilder().build();
    static Gson gson = new GsonBuilder().setDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz").excludeFieldsWithoutExposeAnnotation().create();


    /**
     * Creates a new lecture.
     * @param name      A name of the lecture
     * @return          Lecture which was created, null otherwise
    */
    //  TODO: I AM PASSING A BLANK STRING IN THE POST METHOD, THIS SHOULD BE CHANGED
    public static Lecture createLecture(String name) {
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers
                .ofString("")).uri(URI.create("http://localhost:8080//api/newLecture?name=" + name)).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
        }

        return gson.fromJson(response.body(), Lecture.class);

    }

    /**
     * Sends an HTTP request to get lecture
     * by uuid.
     *
     * @param lectureId The uuid of the lecture
     * @return Lecture if the lecture exists on server or null if it doesn't
     */
    public static Lecture joinLectureById(String lectureId) {
        lectureId = URLEncoder.encode(lectureId, StandardCharsets.UTF_8);


        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create("http://localhost:8080/api/get/" + lectureId)).build();
        HttpResponse<String> response;

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
        return gson.fromJson(response.body(), Lecture.class);
    }

    /**
     * Sends an HTTP request to ask
     * a question with the current LectureID.
     *
     * @param questionText the question text
     * @return the int
     */
    public static int askQuestion(String questionText) {

        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }


        UUID lectureId = Lecture.getCurrentLecture().getUuid();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        QuestionEntity question = new QuestionEntity(lectureId,questionText,  42);

        String json = gson.toJson(question);
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().POST(req).uri(
                URI.create("http://localhost:8080/api/question/ask")).setHeader(
                        "Content-Type", "application/json").build();
        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            e.printStackTrace();
            return -2;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return -3;
        }
        System.out.println("The question was asked successfully! " + response.body());

        return 1;
    }

    public static List<QuestionEntity> fetchQuestions() {

        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return null;
        }


        UUID lectureId = Lecture.getCurrentLecture().getUuid();


        //String json = gson.toJson(question);
        //HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create("http://localhost:8080/api/question/fetch?lid=" + lectureId.toString())).build();
        HttpResponse<String> response;


        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("There was an exception!");
            e.printStackTrace();
            return null;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return null;
        }
        System.out.println("The questions were retrieved successfully! " + response.body());

        Type listType = new TypeToken<List<QuestionEntity>>(){}.getType();

         List<QuestionEntity> list = gson.fromJson(response.body(), listType);

         //questionPane.getItems().addAll(list);
        return list;


    }





}
