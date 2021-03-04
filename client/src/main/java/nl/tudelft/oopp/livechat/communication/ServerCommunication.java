package nl.tudelft.oopp.livechat.communication;

import com.google.gson.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.tudelft.oopp.livechat.data.Lecture;

/**
 *  Class for communicating with server.
 */
public class ServerCommunication {

    private static HttpClient client = HttpClient.newBuilder().build();
    private static Gson gson = new Gson();


    /**
     * Creates a new lecture.
     * @param name      A name of the lecture
     * @return          Lecture which was created, null otherwise
    */
    //  TODO: I AM PASSING A BLANK STRING IN THE POST METHOD, THIS SHOULD BE CHANGED
    public static Lecture createLecture(String name) {
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("")).uri(URI.create("http://localhost:8080//api/newLecture?name=" + name)).build();
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

}
