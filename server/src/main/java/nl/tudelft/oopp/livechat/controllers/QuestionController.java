package nl.tudelft.oopp.livechat.controllers;


import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.services.QuestionService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/question")
public class QuestionController {
    private QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /*@PostMapping("/ask")
    public QuestionEntity askQuestion(@RequestParam String question) {
        return questionService.newQuestionEntity()
    }*/

}
