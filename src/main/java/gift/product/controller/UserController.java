package gift.product.controller;


import gift.product.dto.CreateUserRequest;
import gift.product.dto.LoginRequest;
import gift.product.dto.LoginResponse;
import gift.product.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public Long register(@RequestBody CreateUserRequest req) {
		return userService.register(req);
	}

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest req) {
		return userService.login(req);
	}



}
