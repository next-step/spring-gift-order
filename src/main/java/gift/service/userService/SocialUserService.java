package gift.service.userService;

import gift.entity.SocialUser;
import gift.repository.userRepository.SocialUserRepository;
import org.springframework.stereotype.Service;

@Service
public class SocialUserService {
    private final SocialUserRepository socialUserRepository;

    public SocialUserService(SocialUserRepository socialUserRepository) {
        this.socialUserRepository = socialUserRepository;
    }




    public SocialUser saveSocialUser(String email, String kakaoAccessToken) {
        SocialUser existing = socialUserRepository.findByUserEmail(email);

        if (existing != null) {
            existing.updateToken(kakaoAccessToken);
            return socialUserRepository.save(existing);
        } else {
            SocialUser newUser = new SocialUser(email, kakaoAccessToken);
            return socialUserRepository.save(newUser);
        }
    }

    public SocialUser findByUserEmail(String userEmail) {
        return socialUserRepository.findByUserEmail(userEmail);
    }
}

