package nl.tudelft.oopp.livechat.services;

import nl.tudelft.oopp.livechat.entities.UserEntity;
import nl.tudelft.oopp.livechat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    /**
     * New user.
     *
     * @param user the user
     * @return 0 if successful,
     *          -1 if uid is invalid
     */
    public int newUser(UserEntity user, String ip) {
        user.setIp(ip);
        if (user.getUid() < 0x1000000000L) { // use mac address checksum
            return -1;
        }
        userRepository.save(user);
        return 0;
    }

}
