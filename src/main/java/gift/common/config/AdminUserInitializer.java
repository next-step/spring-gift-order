package gift.common.config;

import gift.domain.Role;
import gift.domain.user.BasicUser;
import gift.domain.user.User;
import gift.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    public AdminUserInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void run(ApplicationArguments args) {
        userRepository.save(User.createBasicUser("admin@admin.com", "1234", Role.ADMIN));
    }
}
