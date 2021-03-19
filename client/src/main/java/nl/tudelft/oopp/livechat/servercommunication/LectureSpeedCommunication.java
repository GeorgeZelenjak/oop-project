package nl.tudelft.oopp.livechat.servercommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.oopp.livechat.data.Lecture;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class LectureSpeedCommunication {

    private static final HttpClient client = HttpClient.newBuilder().build();

    private static final Gson gson = new GsonBuilder().setDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz").excludeFieldsWithoutExposeAnnotation().create();


    public static int getVotesOnLectureSpeed(UUID uuid) {
        return 0;
    }


    /**
     * Vote on lecture speed.
     *
     * @param uid   the uid
     * @param uuid  the uuid
     * @param speed the speed
     * @return the int
     */
    public static int voteOnLectureSpeed(long uid, UUID uuid, String speed) {
        //Check if current lecture has been set
        if (Lecture.getCurrentLecture() == null) {
            System.out.println("You are not connected to a lecture!");
            return -1;
        }
        System.out.println(uid);
        System.out.println(uuid);
        System.out.println(speed);

        //Parameters for request
        HttpRequest.BodyPublisher req =  HttpRequest.BodyPublishers.ofString(speed);
        String address = "http://localhost:8080/api/vote/lectureSpeed";

        //Creating request and defining response
        HttpRequest request = HttpRequest.newBuilder().PUT(req).uri(
                URI.create(address + "?uid=" + uid + "&uuid=" + uuid)).build();

        HttpResponse<String> response;
        //Catching error when communicating with server
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("An exception when trying to communicate with the server!");
            //e.printStackTrace();
            return -2;
        }
        int result = handleResponse(response);
        if (result == 0) {
            System.out.println("Lecture speed was voted on successfully! "
                    + response.body());
        }
        System.out.println("Result is " + result);
        return result;
    }

    private static int resetLectureSpeed(UUID uuid, UUID modkey) {
        return 0;
    }

    private static int handleResponse(HttpResponse<String> response) {
        //Unexpected response
        if (response.statusCode() != 200) {
            System.out.println("Status: " + response.statusCode());
            return -3;
        }

        //Correct response, but not success
        if (!response.body().equals("0")) {
            System.out.println("Response body " + response.body());
            return -4;
        }

        //Success
        return 0;
    }
}
