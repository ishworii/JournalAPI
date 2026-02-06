package com.ishwor.journalapi.service.impl;


import com.ishwor.journalapi.dto.AuthResponse;
import com.ishwor.journalapi.dto.LoginRequest;
import com.ishwor.journalapi.dto.RegisterRequest;
import com.ishwor.journalapi.entity.RefreshTokenEntity;
import com.ishwor.journalapi.entity.Role;
import com.ishwor.journalapi.entity.UserEntity;
import com.ishwor.journalapi.exception.EmailAlreadyExistsException;
import com.ishwor.journalapi.repository.UserRepository;
import com.ishwor.journalapi.service.AuthService;
import com.ishwor.journalapi.service.JwtService;
import com.ishwor.journalapi.service.RefreshTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        Role role = Role.USER;
        if(request.getRole() != null){
            role = Role.valueOf(request.getRole().toUpperCase());
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(role);

        UserEntity saved = userRepository.save(userEntity);

        // Generate short-lived access token (15 minutes)
        String accessToken = jwtService.generateToken(saved.getId(), saved.getEmail(), saved.getRole());

        // Create and save long-lived refresh token (24 hours)
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.createRefreshToken(saved.getEmail());

        return new AuthResponse(accessToken, refreshTokenEntity.getToken());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(),userEntity.getPasswordHash())){
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate short-lived access token (15 minutes)
        String accessToken = jwtService.generateToken(userEntity.getId(), userEntity.getEmail(), userEntity.getRole());

        // Create and save long-lived refresh token (24 hours)
        // Note: This will replace any existing refresh token due to @OneToOne relationship
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.createRefreshToken(userEntity.getEmail());

        return new AuthResponse(accessToken, refreshTokenEntity.getToken());
    }
}
