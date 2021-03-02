package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuestionService {
    QuestionRepository questionRepository;

    @Autowired
    LectureRepository lectureRepository;

    public List<QuestionEntity> getQuestionsByLectureId(UUID lectureId) {
        return questionRepository.findAllByLectureId(lectureId);
    }

    public QuestionEntity newQuestionEntity(LectureEntity lecture, String text, String answerText, LocalDateTime time, String ownerId) {
        QuestionEntity q = new QuestionEntity(lecture, text, answerText, time, ownerId);
        questionRepository.save(q);
        return q;
    }

    public QuestionEntity deleteQuestion(long id, String personId) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if(q == null || !q.getOwnerId().equals(personId)) return null;
        questionRepository.deleteById(id);
        return q;
    }
}
