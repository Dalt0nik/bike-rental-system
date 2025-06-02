package lt.psk.bikerental.controller;


import lombok.RequiredArgsConstructor;
import lt.psk.bikerental.DTO.Check.ExpenseDTO;
import lt.psk.bikerental.DTO.Check.CheckDTO;
import lt.psk.bikerental.service.CheckService;
import lt.psk.bikerental.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/check")
@RequiredArgsConstructor
public class CheckController {

    private final CheckService checkService;

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<CheckDTO>> getAllChecks(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(checkService.getAllChecks(jwt));
    }

    @GetMapping("/latest")
    public ResponseEntity<CheckDTO> getLatestCheck(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(checkService.getLatestCheck(jwt));
    }

    @GetMapping("/expenses")
    public List<ExpenseDTO> getAllBillings(@AuthenticationPrincipal Jwt jwt) {
        return expenseService.getAllExpenses(jwt);
    }

}
