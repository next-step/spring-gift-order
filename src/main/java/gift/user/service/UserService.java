package gift.user.service;

import gift.shared.exception.user.NoUserException;
import gift.user.dto.request.UserModifyRequest;
import gift.user.dto.response.UserResponse;
import gift.shared.exception.product.NoValueException;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static gift.user.status.UserStatus.NO_VALUE;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUserInfo(Long userId) {
        return UserResponse.from(userRepository.findById(userId)
                .orElseThrow(() -> new NoValueException(NO_VALUE.getMessage()
        )));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public void modifyUserInfo(Long userId, UserModifyRequest userModifyRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new NoUserException(NO_VALUE.getMessage()));
        user.modifyUser(userModifyRequest);
    }

    public void deleteById(Long userId){
        userRepository.deleteById(userId);
    }
}
