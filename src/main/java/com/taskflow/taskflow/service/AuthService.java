package com.taskflow.taskflow.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskflow.taskflow.dto.RegisterRequest;
import com.taskflow.taskflow.entity.Role;
import com.taskflow.taskflow.entity.User;
import com.taskflow.taskflow.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.taskflow.taskflow.dto.LoginRequest;
import com.taskflow.taskflow.dto.LoginResponse;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtEncoder jwtEncoder;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtEncoder = jwtEncoder;
	}

	public String register(RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email is already registered");
		}

		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.USER);

		userRepository.save(user);

		return "User registered successfully";
	}

	public LoginResponse login(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

		if (!user.isEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}

		Instant now = Instant.now();
		long expiresIn = 3600;

		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("taskflow-api").issuedAt(now)
				.expiresAt(now.plus(expiresIn, ChronoUnit.SECONDS)).subject(user.getEmail())
				.claim("role", user.getRole().name()).build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
		        .build();

		String token = jwtEncoder.encode(
		        JwtEncoderParameters.from(header, claims)
		).getTokenValue();

		return new LoginResponse(token, "Bearer", expiresIn);
	}

}