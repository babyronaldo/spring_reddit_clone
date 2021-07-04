package com.example.springredditclone.controller;

import com.example.springredditclone.dto.AuthenticationResponse;
import com.example.springredditclone.dto.LoginRequest;
import com.example.springredditclone.dto.RegisterRequest;
import com.example.springredditclone.exception.ConflictException;
import com.example.springredditclone.exception.EntityNotFoundException;
import com.example.springredditclone.exception.InvalidRequestException;
import com.example.springredditclone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest) throws ConflictException, InvalidRequestException {
        authService.signup(registerRequest);
        return new ResponseEntity(OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) throws EntityNotFoundException {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", OK);
    }

    // login API endpoint
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
