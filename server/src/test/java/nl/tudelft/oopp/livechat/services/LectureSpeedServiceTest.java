package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.repositories.*;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LectureSpeedServiceTest {
    @Autowired
    LectureSpeedService lectureSpeedService;

    @Autowired
    UserLectureSpeedRepository userLectureSpeedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LectureRepository lectureRepository;


}
