package gift.product.service;


import gift.product.dto.CreateUserRequest;
import gift.product.dto.LoginRequest;
import gift.product.dto.LoginResponse;
import gift.product.entity.User;
import gift.product.repository.UserRepository;
import gift.product.commons.utils.JwtUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;


	public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
	}

	public Long register(CreateUserRequest req) {
		if(userRepository.findByEmail(req.email()).isPresent()){
			throw new DataIntegrityViolationException("이미 사용중인 이메일입니다.");
		}

		if(userRepository.findByNickname(req.nickName()).isPresent()){
			throw new DataIntegrityViolationException("이미 사용중인 닉네임입니다.");
		}

		String encodedPassword = passwordEncoder.encode(req.password());

		User user = new User(req.email(), encodedPassword, req.nickName());
		User saved = userRepository.save(user);
		return saved.getId();

	}


	public LoginResponse login(LoginRequest req) {
		Optional<User> user = userRepository.findByEmail(req.email());

		if(user.isEmpty() || !passwordEncoder.matches(req.password(), user.get().getPassword())){
			throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못됐습니다.");
		}

		User foundUser = user.get();
		String token = jwtUtil.generateToken(foundUser);

		return new LoginResponse(token);
	}

}
