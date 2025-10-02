package com.pgms.api;

import com.pgms.dto.AuthDtos.*;
import com.pgms.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService svc;

    public AuthController(AuthService svc){ this.svc = svc; }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest req){
        log.debug("Signup {}", req.email);
        svc.signup(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req){
        var res = svc.login(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest req){
        svc.changePassword(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/enable-totp")
    public ResponseEntity<EnableTotpResponse> enableTotp(@RequestParam String login,
                                                         @RequestParam(required = false) String issuer){
        return ResponseEntity.ok(svc.enableTotp(login, issuer));
    }

    @PostMapping("/admin-reset")
    public ResponseEntity<Void> adminReset(@RequestBody @Valid AdminResetRequest req){
        svc.adminReset(req);
        return ResponseEntity.noContent().build();
    }
}
