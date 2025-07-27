package gift.service.userService;

import gift.Jwt.JwtUtil;
import gift.entity.User;
import gift.entity.UserRole;
import gift.exception.userException.UserDuplicatedException;
import gift.exception.userException.UserNotFoundException;
import gift.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }


    @Override
    @Transactional
    public String registerUser(User user) {
        isEmailExist(user.getEmail());

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);

        return token;
    }

    private void isEmailExist(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserDuplicatedException();
        }
    }


    @Override
    public String loginUser(String email, String password) {
        User findUser = findUserByEmail(email);

        findUser.checkPassword(password);

        return jwtUtil.generateToken(findUser);

    }

    @Override
    public User findUserByEmail(String userEmail) {
        return userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));
    }

    @Override
    public User saveSocialUser(String email) {
        User user = new User(email, "1234", UserRole.USER);
        return userRepository.save(user);
    }


    @Override
    public Page<User> getUserList(String email, Long loginId, Pageable pageable) {

        findUserById(loginId).checkAuthorization();

        if (email != null) {
            return getUsersByEmail(email, pageable);
        }

        return getAllUser(pageable);
    }

    private Page<User> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    private Page<User> getUsersByEmail(String email, Pageable pageable) {

        Page<User> users = userRepository.findByEmailContaining(email, pageable);
        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }

        return users;
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    @Transactional
    public User updateUser(Long id, String email, String password, Long loginId) {
        findUserById(loginId).checkAuthorization();

        User findUser = findUserById(id);

        User updatedUser = findUser.updateFrom(email, password);

        return userRepository.save(updatedUser);
    }


    @Override
    @Transactional
    public void deleteUserById(Long id, Long loginId) {
        findUserById(loginId).checkAuthorization();

        userRepository.deleteUserById(id);
    }

}
