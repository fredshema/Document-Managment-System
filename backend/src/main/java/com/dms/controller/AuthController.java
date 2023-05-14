package com.dms.controller;

import com.dms.model.Auth.AuthResponse;
import com.dms.model.Auth.LoginRequest;
import com.dms.model.Auth.RegisterRequest;
import com.dms.model.User;
import com.dms.repository.UserRepository;
import com.dms.service.CustomUserDetailsService;
import com.dms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            Map<String, String> message = new HashMap<>();
            message.put("message", "Incorrect username or password");
            return ResponseEntity.badRequest().body(message);
        } catch (Exception e) {
            Map<String, String> message = new HashMap<>();
            message.put("message", "Something went wrong");
            return ResponseEntity.badRequest().body(message);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        final User user = userRepository.findUserByUsername(request.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt, user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws Exception {
        Map<String, String> message = new HashMap<>();
        if (userRepository.findByUsername(request.getUsername()) != null) {
            message.put("message", "Username already exists");
            return ResponseEntity.badRequest().body(message);
        }
        if (userRepository.findByEmail(request.getEmail()) != null) {
            message.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(message);
        }
        User user = request.toUser();
        final String password = user.getPassword();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        String token = jwtUtil.generateToken(userDetailsService.loadUserByUsername(user.getUsername()));

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

}
