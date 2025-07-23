package gift.controller;

import gift.dto.TokenResponse;
import gift.dto.UserRequest;
import gift.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody UserRequest userRequest) {
        String token = userService.register(userRequest);
        return new ResponseEntity<>(new TokenResponse(token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserRequest userRequest) {
        String token = userService.login(userRequest);
        return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
    }
}
