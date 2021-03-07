package nl.tudelft.oopp.livechat.controllers;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.services.LectureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
public class LectureController {

    private final LectureService service;

    /**.
     * Constructor for the lecture controller.
     * @param service lecture service
     */
    public LectureController(LectureService service) {
        this.service = service;
    }

    /**.
     * GET Endpoint to retrieve a lecture.
     * @return selected lecture
     */
    @GetMapping("/api/get/{id}")
    public LectureEntity getLecturesByID(@PathVariable("id") UUID id) {
        return service.getLectureById(id);
    }

    /**.
     * POST Endpoint to create a new lecture.
     * @param name the name of the lecture
     * @return a new lecture entity
     */
    @PostMapping("/api/newLecture")
    public LectureEntity newLecture(@RequestParam String name) {
        return service.newLecture(name, "placeholder"); //these are placeholders
    }

    /**.
     * DELETE Endpoint to delete a lecture with the specified id iff the moderator key is correct.
     * @param modkey the moderator key to authenticate
     * @param id UUID of lecture
     * @return 0 if lecture is deleted successfully, -1 if not
     */
    @DeleteMapping("/api/delete/{id}/{modkey}")
    public int delete(@PathVariable("modkey") UUID modkey, @PathVariable("id") UUID id) {
        return service.delete(id, modkey);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> badUUID(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid "
                + "UUID");
    }
}
