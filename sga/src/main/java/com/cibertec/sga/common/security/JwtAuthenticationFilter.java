package com.cibertec.sga.common.security;

import com.cibertec.sga.user.domain.model.User;
import com.cibertec.sga.user.domain.repository.IUserRepository;
import com.cibertec.sga.user.infrastructure.persistence.UserJpaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lee el header {@code Authorization: Bearer <token>}, valida el JWT y re-resuelve el usuario
 * contra la base de datos en cada request (rol y estado activo nunca se confían solo al JWT —
 * RNF-02). Si el token falta, es inválido, o el usuario ya no está activo, simplemente no se
 * autentica la request: los endpoints protegidos la rechazan más adelante en la cadena.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final IJwtService jwtService;
    private final IUserRepository userRepository;
    private final UserJpaRepository userJpaRepository;

    public JwtAuthenticationFilter(
        IJwtService jwtService, IUserRepository userRepository, UserJpaRepository userJpaRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        extractBearerToken(request)
            .flatMap(jwtService::validateAndGetSubject)
            .flatMap(this::resolveAuthenticatedUser)
            .ifPresent(authenticatedUser -> {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + authenticatedUser.roleName()));
                var authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private Optional<AuthenticatedUser> resolveAuthenticatedUser(UUID userUuid) {
        Optional<User> user = userRepository.findByUuid(userUuid).filter(User::isActive);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        return userJpaRepository.findByUuid(userUuid).map(entity -> new AuthenticatedUser(
            entity.getId(), userUuid, user.get().getUsername(), user.get().getRoleName()
        ));
    }
}
