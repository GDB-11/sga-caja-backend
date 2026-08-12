package com.cibertec.sga.auth.application;

import com.cibertec.sga.auth.domain.error.AuthError;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.security.IJwtService;
import com.cibertec.sga.common.security.TokenHasher;
import com.cibertec.sga.refreshtoken.domain.model.RefreshToken;
import com.cibertec.sga.refreshtoken.domain.repository.IRefreshTokenRepository;
import com.cibertec.sga.user.domain.model.User;
import com.cibertec.sga.user.domain.repository.IUserRepository;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IJwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final Duration refreshTokenTtl;

    public AuthService(
        IUserRepository userRepository,
        IRefreshTokenRepository refreshTokenRepository,
        IJwtService jwtService,
        PasswordEncoder passwordEncoder,
        @Value("${sga.security.jwt.refresh-token-ttl-minutes}") long refreshTokenTtlMinutes
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenTtl = Duration.ofMinutes(refreshTokenTtlMinutes);
    }

    @Override
    public Result<AuthSession, AuthError> login(String username, String rawPassword) {
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty() || !passwordEncoder.matches(rawPassword, maybeUser.get().getPasswordHash())) {
            return Result.failure(new AuthError.InvalidCredentials());
        }

        User user = maybeUser.get();
        if (!user.isActive()) {
            return Result.failure(new AuthError.UserInactive());
        }

        return Result.success(issueSession(user));
    }

    @Override
    public Result<AuthSession, AuthError> refresh(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return Result.failure(new AuthError.InvalidRefreshToken());
        }

        String tokenHash = TokenHasher.sha256Hex(rawRefreshToken);
        Optional<RefreshToken> maybeToken = refreshTokenRepository.findByTokenHash(tokenHash);
        if (maybeToken.isEmpty()) {
            return Result.failure(new AuthError.InvalidRefreshToken());
        }

        RefreshToken token = maybeToken.get();
        if (token.isRevoked()) {
            refreshTokenRepository.revokeAllActiveByUserUuid(token.getUserUuid());
            return Result.failure(new AuthError.RefreshTokenReused());
        }
        if (token.isExpired()) {
            return Result.failure(new AuthError.InvalidRefreshToken());
        }

        Optional<User> maybeUser = userRepository.findByUuid(token.getUserUuid()).filter(User::isActive);
        if (maybeUser.isEmpty()) {
            return Result.failure(new AuthError.InvalidRefreshToken());
        }

        refreshTokenRepository.revokeByTokenHash(tokenHash);
        return Result.success(issueSession(maybeUser.get()));
    }

    @Override
    public Result<Void, AuthError> logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokenRepository.revokeByTokenHash(TokenHasher.sha256Hex(rawRefreshToken));
        }
        return Result.success(null);
    }

    @Override
    public Result<User, AuthError> me(UUID userUuid) {
        return userRepository.findByUuid(userUuid)
            .map(Result::<User, AuthError>success)
            .orElseGet(() -> Result.failure(new AuthError.UserNotFound(userUuid.toString())));
    }

    private AuthSession issueSession(User user) {
        String accessToken = jwtService.generateAccessToken(user.getUuid());
        String rawRefreshToken = generateOpaqueToken();

        refreshTokenRepository.insert(
            RefreshToken.builder()
                .userUuid(user.getUuid())
                .tokenHash(TokenHasher.sha256Hex(rawRefreshToken))
                .expiresAt(Instant.now().plus(refreshTokenTtl))
                .build()
        );

        return new AuthSession(
            accessToken,
            jwtService.getAccessTokenExpirySeconds(),
            rawRefreshToken,
            refreshTokenTtl.toSeconds(),
            user
        );
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
