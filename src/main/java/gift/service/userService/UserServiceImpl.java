package gift.service.userService;

import gift.Jwt.JwtUtil;
import gift.dto.userDto.UserLoginDto;
import gift.dto.userDto.UserRegisterDto;
import gift.dto.userDto.UserUpdateDto;
import gift.entity.User;
import gift.exception.userException.UserAuthorizationException;
import gift.exception.userException.UserDuplicatedException;
import gift.exception.userException.UserNotFoundException;
import gift.exception.userException.UserPasswordInputException;
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
    public String registerUser(UserRegisterDto userRegisterDto) {
        User user = userRegisterDto.dtoToUser();

        if (isEmailExist(user.getEmail())) {
            throw new UserDuplicatedException();
        }
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);

        return token;
    }

    private boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public String loginUser(UserLoginDto userLoginDto) {
        String targetEmail = userLoginDto.email();

        User findUser = findUserByEmail(targetEmail);

        if (!findUser.checkPassword(userLoginDto.password())) {
            throw new UserPasswordInputException();
        }
        return jwtUtil.generateToken(findUser);

    }

    @Override
    public User findUserByEmail(String userEmail) {
        User user = userRepository.findUserByEmail(userEmail);

        if (user == null) {
            throw new UserNotFoundException(userEmail);
        }

        return user;
    }

    @Override
    public Page<User> getUserList(String email, boolean isAdmin, Pageable pageable) {

        if (!isAdmin) {
            throw new UserAuthorizationException();
        }
        if (email != null) {
            return getUsersByEmail(email, pageable);
        }

        return getAllUser(pageable);
    }

    private Page<User> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    private Page<User> getUsersByEmail(String email, Pageable pageable) {
        if (email == null) {
            throw new UserNotFoundException();
        } else {
            Page<User> users = userRepository.findByEmailContaining(email, pageable);
            if (users.isEmpty()) {
                throw new UserNotFoundException();
            }
            return users;
        }
    }

    @Override
    public User finUserById(Long id) {
        User user = findUserById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    private User findUserById(Long id) {
        User user = userRepository.findUserById(id);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateDto userUpdateDto, boolean isAdmin) {
        if (!isAdmin) {
            throw new UserAuthorizationException();
        }

        User findUser = userRepository.findById(id).orElse(null);

        if (findUser == null) {
            throw new UserNotFoundException();
        }

        User updatedUser = findUser.updateFrom(userUpdateDto);

        return userRepository.save(updatedUser);
    }


    @Override
    @Transactional
    public void deleteUserById(Long id, boolean isAdmin) {
        if (!isAdmin) {
            throw new UserAuthorizationException();
        }

        userRepository.deleteUserById(id);
    }

}
