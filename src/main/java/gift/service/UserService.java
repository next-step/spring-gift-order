package gift.service;

import gift.dto.UserRequestDto;
import gift.dto.UserResponseDto;
import gift.entity.User;
import gift.exception.DecryptFailedException;
import gift.exception.EncryptFailedException;
import gift.exception.NotFoundException;
import gift.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public List<UserResponseDto> findAllUsers(){
        return userRepository.findAll().stream()
                .map(user -> {
                    try {
                        return new UserResponseDto(
                                user.getId(),
                                authService.decryptAES(user.getEmail()),
                                user.getPassword(),
                                user.getCreatedDate()
                        );
                    } catch (Exception e) {
                        throw new DecryptFailedException();
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
        String email;
        // email 복호화
        try {
            email = authService.decryptAES(user.getEmail());
        } catch (Exception e) {
            throw new DecryptFailedException();
        }

        return new UserResponseDto(user.getId(), email, user.getPassword(), user.getCreatedDate());
    }

    @Transactional
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto){
        String email;
        String password;

        try {
            email = authService.encryptAES(userRequestDto.email());
            password = authService.encryptSHA256(userRequestDto.password());
        }
        catch (Exception e) {throw new EncryptFailedException();
        }

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
        user.update(email, password);
        return new UserResponseDto(user);
    }
}
