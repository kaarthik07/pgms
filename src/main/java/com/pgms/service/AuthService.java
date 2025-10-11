package com.pgms.service;

import com.pgms.domain.Role;
import com.pgms.domain.User;
import com.pgms.dto.AuthDtos.*;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.UserRepo;
import com.pgms.util.JwtUtil;
import com.pgms.util.PasswordUtil;
import com.pgms.util.TotpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepo users;
    private final String jwtSecret;
    private final long tokenTtlSec;
    private final String adminResetCode;

    public AuthService(UserRepo users,
                       @Value("${auth.jwtSecret:dev-secret}") String jwtSecret,
                       @Value("${auth.tokenTtlMinutes:240}") long tokenTtlMinutes,
                       @Value("${auth.adminResetCode:CHANGE_ME}") String adminResetCode) {
        this.users = users;
        this.jwtSecret = jwtSecret;
        this.tokenTtlSec = Duration.ofMinutes(tokenTtlMinutes).toSeconds();
        this.adminResetCode = adminResetCode;
    }

    // --- signup (admin creates users or you can expose temporarily) ---
    @Transactional
    public void signup(SignupRequest req) {
        if (users.existsByEmail(req.email.toLowerCase())) {
            throw new BadRequestException("Email already registered");
        }
        if (req.phone != null && !req.phone.isBlank() && users.existsByPhone(req.phone)) {
            throw new BadRequestException("Phone already registered");
        }
        User u = new User();
        u.setEmail(req.email);
        u.setPhone(req.phone);
        u.setRole(req.role == null ? Role.TENANT : req.role);
        u.setPasswordHash(PasswordUtil.hash(req.tempPassword));
        users.save(u);
        log.info("Created user {} role={}", u.getEmail(), u.getRole());
    }

    // --- login ---
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        var u = users.findByLoginId(req.login)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean okPwd = PasswordUtil.verify(req.password, u.getPasswordHash());
        if (!okPwd) throw new BadRequestException("Invalid credentials");

        boolean totpEnabled = u.getTotpSecret() != null;
        if (totpEnabled) {
            if (req.totp == null || !TotpUtil.verifyCode(u.getTotpSecret(), req.totp, System.currentTimeMillis(), 1)) {
                var r = new LoginResponse();
                r.requireTotp = true;
                r.message = "TOTP required or invalid";
                return r;
            }
        }
        String token = JwtUtil.createToken(
                Map.of("uid", u.getId().toString(), "role", u.getRole().name()),
                u.getEmail(), tokenTtlSec, jwtSecret);

        var r = new LoginResponse();
        r.token = token;
        r.requireTotp = false;
        r.message = "OK";
        return r;
    }

    // --- change password ---
    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        var u = users.findByLoginId(req.login)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!PasswordUtil.verify(req.oldPassword, u.getPasswordHash())) {
            throw new BadRequestException("Old password is incorrect");
        }
        u.setPasswordHash(PasswordUtil.hash(req.newPassword));
        users.save(u);
        log.info("Password changed for {}", u.getEmail());
    }

    // --- enable TOTP (returns secret & otpauth URL); call once then verify by login with code ---
    @Transactional
    public EnableTotpResponse enableTotp(String login, String issuer) {
        var u = users.findByLoginId(login)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String secret = TotpUtil.newBase32Secret();
        u.setTotpSecret(secret);
        users.save(u);

        var res = new EnableTotpResponse();
        res.secretBase32 = secret;
        res.otpauthUrl = TotpUtil.otpauthUrl(issuer == null ? "PGMS" : issuer, u.getEmail(), secret);
        return res;
    }

    // --- admin temp reset ---
    @Transactional
    public void adminReset(AdminResetRequest req) {
        if (!adminResetCode.equals(req.adminResetCode))
            throw new BadRequestException("Invalid admin reset code");
        var u = users.findByLoginId(req.login)
                .orElseThrow(() -> new NotFoundException("User not found"));
        u.setPasswordHash(PasswordUtil.hash(req.newTempPassword));
        users.save(u);
        log.warn("Admin reset password for {}", u.getEmail());
    }
}
