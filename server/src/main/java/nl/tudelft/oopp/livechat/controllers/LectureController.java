package nl.tudelft.oopp.livechat.controllers;

import java.time.LocalDateTime;
import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.services.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
public class LectureController {

    @Autowired
    private LectureService service;


    /**
     * GET Endpoint to retrieve a lecture.
     *
     * @return selected {@link nl.tudelft.oopp.livechat.entities.LectureEntity}.
     */
    @GetMapping("/api/get/{id}")
    public LectureEntity getLecturesByID(@PathVariable("id") String id) {
        return service.getLectureById(id);
    }

    @PostMapping("/api/newLecture")
    public LectureEntity newLecture(@RequestParam String name) {
        return service.newLecture(name, "placeholder");
        //these are placeholders
    }



    /**
     * Deletes a lecture with UUID id iff the modkey key is correct.
     * @param modkey the modkey to authenticate
     * @param id uuid of lecture
     * @return 0 if deleted, -1 if not
     */
    @DeleteMapping("/api/delete/{id}/{modkey}")
    public int delete(@PathVariable("modkey") String modkey, @PathVariable("id") String id) {
        LectureEntity toDelete = service.getLectureById(id);
        if (toDelete != null && toDelete.getModkey().equals(modkey)) {
            service.delete(id);
            return 0;
        }
        return -1;
    }
}
