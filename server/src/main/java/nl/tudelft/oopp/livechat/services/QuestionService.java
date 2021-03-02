package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.LectureEntity;
import nl.tudelft.oopp.livechat.entities.QuestionEntity;
import nl.tudelft.oopp.livechat.repositories.LectureRepository;
import nl.tudelft.oopp.livechat.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    LectureRepository lectureRepository;

    public List<QuestionEntity> getQuestionsByLectureId(UUID lectureId) {
        return questionRepository.findAllByLectureId(lectureId);
    }

    public QuestionEntity newQuestionEntity(UUID lectureId, String text, String answerText, LocalDateTime time, long ownerId) {
        QuestionEntity q = new QuestionEntity(lectureId, text, answerText, time, ownerId);
        questionRepository.save(q);
        return q;
    }

    public int deleteQuestion(long id, long personId) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if(q == null || q.getOwnerId() != personId) return -1;
        questionRepository.deleteById(id);
        return 0;
    }

    public int deleteModeratorQuestion(long id, String modkey) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) return -1;
        LectureEntity l = lectureRepository.findLectureEntityByUuid(q.getLecture());
        UUID modk = UUID.fromString(modkey);
        if (l.getModkey().equals(modk)) {
            questionRepository.deleteById(id);
            return 0;
        }
        return -1;
    }

    public int editQuestion(long id, String moderatorKey, String new_text, long new_ownerId) {
        QuestionEntity q = questionRepository.findById(id).orElse(null);
        if (q == null) {
            return -1;
        }
        LectureEntity lecture = lectureRepository.findLectureEntityByUuid(q.getLecture());
        UUID modk = UUID.fromString(moderatorKey);
        if(lecture.getModkey().equals(modk)) {
            q.setText(new_text);
            q.setOwnerId(new_ownerId);
            return 0;
        }
        return -1;
    }
}
