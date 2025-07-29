package gift.controller.userController;


import gift.Jwt.TokenUtils;
import gift.config.Interceptor.AdminOnly;
import gift.dto.userDto.UserLoginDto;
import gift.dto.userDto.UserRegisterDto;
import gift.dto.userDto.UserUpdateDto;
import gift.entity.User;
import gift.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final TokenUtils tokenUtils;

    public UserController(UserService userService, TokenUtils tokenUtils) {
        this.userService = userService;
        this.tokenUtils = tokenUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody @Valid UserRegisterDto dto) {
        String token = userService.registerUser(dto.dtoToUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody @Valid UserLoginDto dto) {
        System.out.println(dto.email());
        System.out.println(dto.password());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", userService.loginUser(dto.email(), dto.password())));
    }


    @AdminOnly
    @GetMapping()
    public ResponseEntity<?> getUserList(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestParam(required = false) String email, Pageable pageable, Model model) {
        String token = tokenUtils.extractToken(authHeader);
        tokenUtils.validateToken(token);
        Long loginId = tokenUtils.extractUserId(token);

        Page<User> users = userService.getUserList(email, loginId, pageable);
        return ResponseEntity.ok(users);
    }

    @AdminOnly
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestParam Long id, Model model) {
        String token = tokenUtils.extractToken(authHeader);
        tokenUtils.validateToken(token);
        Long loginId = tokenUtils.extractUserId(token);
        userService.deleteUserById(id, loginId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @AdminOnly
    @PutMapping("/{id}/edit")
    public ResponseEntity<User> updateUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id, @RequestBody @Valid UserUpdateDto dto) {
        String token = tokenUtils.extractToken(authHeader);
        tokenUtils.validateToken(token);
        Long loginToken = tokenUtils.extractUserId(token);
        String email = dto.email();
        String password = dto.password();

        User updatedUser = userService.updateUser(id, email, password, loginToken);

        return ResponseEntity.ok(updatedUser);
    }
}
