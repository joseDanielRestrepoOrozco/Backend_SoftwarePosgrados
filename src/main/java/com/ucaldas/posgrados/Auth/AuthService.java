package com.ucaldas.posgrados.Auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ucaldas.posgrados.Jwt.JwtService;
import com.ucaldas.posgrados.User.Rol;
import com.ucaldas.posgrados.User.Usuario;
import com.ucaldas.posgrados.User.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticacionManager;

    public AuthResponse login(LoginRequest loginRequest) {
        authenticacionManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        UserDetails usuario = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        String token = jwtService.getToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse registro(RegisterRequest registerRequest) {
        Usuario usuario = Usuario.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .nombre(registerRequest.getNombre())
                .apellido(registerRequest.getApellido())
                .email(registerRequest.getEmail())
                .rol(Rol.USUARIO)
                .build();

        userRepository.save(usuario);
        return AuthResponse.builder()
                .token(jwtService.getToken(usuario))
                .build();

    }

}