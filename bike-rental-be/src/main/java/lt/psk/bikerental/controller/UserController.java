package lt.psk.bikerental.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lt.psk.bikerental.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@AuthenticationPrincipal Jwt jwt)
    {
        boolean isCreated = userService.insertUser(jwt);
        return isCreated
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
