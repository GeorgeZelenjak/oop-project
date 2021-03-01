package nl.tudelft.oopp.livechat.controllers;

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
    @GetMapping("/get/{id}")
    public LectureEntity getLecturesByID(@PathVariable("id") String id) {
        return service.getLectureById(id);
    }

    @PostMapping("/post")
    public LectureEntity newLecture(){
        return service.newLecture();
    }

    @DeleteMapping("/del/{id}/{modkey}")
    public int delete(@PathVariable("modkey") String key, @PathVariable("id") String id){
        LectureEntity toDelete = service.getLectureById(id);
        if (toDelete.getModkey().equals(key)){
            service.delete(id);
            return 0;
        }
        return -1;
    }
}
