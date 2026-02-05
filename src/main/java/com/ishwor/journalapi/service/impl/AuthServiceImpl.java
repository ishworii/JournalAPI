package com.ishwor.journalapi.service.impl;


import com.ishwor.journalapi.dto.AuthResponse;
import com.ishwor.journalapi.dto.LoginRequest;
import com.ishwor.journalapi.dto.RegisterRequest;
import com.ishwor.journalapi.entity.Role;
import com.ishwor.journalapi.entity.UserEntity;
import com.ishwor.journalapi.repository.UserRepository;
import com.ishwor.journalapi.service.AuthService;
import com.ishwor.journalapi.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already in use");
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
        String token = jwtService.generateToken(saved.getId(),saved.getEmail(),saved.getRole());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if(!passwordEncoder.matches(request.getPassword(),userEntity.getPasswordHash())){
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtService.generateToken(userEntity.getId(),userEntity.getEmail(),userEntity.getRole());
        return new AuthResponse(token);
    }
}
