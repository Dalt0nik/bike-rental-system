package lt.psk.bikerental.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Check.CheckDTO;
import lt.psk.bikerental.entity.Check;
import lt.psk.bikerental.repository.CheckRepository;
import lt.psk.bikerental.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/check")
@RequiredArgsConstructor
public class CheckController {

    private final CheckRepository checkRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<CheckDTO>> getAllChecks(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = userService.getUserIdFromJwt(jwt);

        List<CheckDTO> checks = checkRepository.findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(check -> modelMapper.map(check, CheckDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(checks);
    }

    @GetMapping("/latest")
    public ResponseEntity<CheckDTO> getLatestCheck(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = userService.getUserIdFromJwt(jwt);

        Check latestCheck = checkRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new EntityNotFoundException("No checks found for this user"));

        CheckDTO dto = modelMapper.map(latestCheck, CheckDTO.class);
        return ResponseEntity.ok(dto);
    }
}
