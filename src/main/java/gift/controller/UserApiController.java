package gift.controller;

import gift.common.interceptor.AdminOnly;
import gift.dto.jwt.TokenResponse;
import gift.dto.user.ChangePasswordRequest;
import gift.dto.user.ChangeRoleRequest;
import gift.dto.user.CreateUserRequest;
import gift.dto.user.LoginRequest;
import gift.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid CreateUserRequest request) {
        userService.saveUser(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse tokenResponse = userService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin")
    @AdminOnly
    public ResponseEntity<Void> changeRole(@RequestBody @Valid ChangeRoleRequest request) {
        userService.changeRole(request);
        return ResponseEntity.ok().build();
    }
}
