package nl.tudelft.oopp.livechat.controllers;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.services.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LectureController {

    @Autowired
    private LectureService service;


    /**
     * GET Endpoint to retrieve a random quote.
     *
     * @return selected {@link nl.tudelft.oopp.livechat.entities.LectureEntity}.
     */
    @GetMapping("/get/{id}")
    public LectureEntity getLecturesByID(@PathVariable("id") String id) {
        return service.getLectureById(id);
    }

    @PostMapping("/post")
    private LectureEntity newLecture(){
        return service.newLecture();
    }
}
