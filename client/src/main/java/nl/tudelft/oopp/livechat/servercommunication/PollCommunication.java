package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import nl.tudelft.oopp.livechat.businesslogic.CommonCommunication;
import nl.tudelft.oopp.livechat.data.Lecture;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class PollCommunication {
    private static final HttpClient client = HttpClient.newBuilder().build();

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();

    private static final String ADDRESS = CommonCommunication.ADDRESS;


    /**
     * Gets votes on lecture speed.
     * @param uuid the id of the lecture
     * @return List of votes on lecture speed: [0] is the faster count, [1] slower count
     *         null if there was an error while trying to communicate with the server
     */
    public static List<Integer> getVotesOnLectureSpeed(UUID uuid) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return null;
        }

        //Parameters for request
        String address = ADDRESS + "/api/vote/getLectureSpeed/";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().GET().uri(
                URI.create(address + uuid)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return null;
        }
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return null;
        }
        System.out.println("Lecture speed votes were retrieved successfully! " + response.body());

        Type listType = new TypeToken<List<Integer>>(){}.getType();
        return gson.fromJson(response.body(), listType);
    }
}
