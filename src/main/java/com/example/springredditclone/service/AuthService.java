package com.example.springredditclone.service;

import com.example.springredditclone.dto.AuthenticationResponse;
import com.example.springredditclone.dto.LoginRequest;
import com.example.springredditclone.dto.RefreshTokenRequest;
import com.example.springredditclone.dto.RegisterRequest;
import com.example.springredditclone.exception.ConflictException;
import com.example.springredditclone.exception.EntityNotFoundException;
import com.example.springredditclone.exception.InvalidRequestException;
import com.example.springredditclone.exception.SpringRedditException;
import com.example.springredditclone.exception.UsernameNotFoundException;
import com.example.springredditclone.model.NotificationEmail;
import com.example.springredditclone.model.User;
import com.example.springredditclone.model.VerificationToken;
import com.example.springredditclone.repository.UserRepository;
import com.example.springredditclone.repository.VerificationTokenRepository;
import com.example.springredditclone.security.JWTProvider;
import com.example.springredditclone.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.example.springredditclone.util.Constants.ACTIVATION_EMAIL;
import static java.time.Instant.now;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository              userRepository;
    private PasswordEncoder             passwordEncoder;
    private VerificationTokenRepository verificationTokenRepository;
    private MailContentBuilder          mail;
    private MailService                 mailService;
    private AuthenticationManager       authenticationManager;
    private JWTProvider                 jwtProvider;
    private RefreshTokenService         refreshTokenService;

    @Transactional
    public void signup(RegisterRequest registerRequest) throws ConflictException, InvalidRequestException {
        // check for unique username
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ConflictException("The username is already exist!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodePassword(registerRequest.getPassword()));
        user.setCreated(now());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);

        String message = mail.build("Thank you for signing up to Spring Reddit, please click on the below url to activate your account : "
                + ACTIVATION_EMAIL + "/" + token);

        mailService.sendMail(new NotificationEmail("Please active your account!", user.getEmail(), message));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);

        return token;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void verifyAccount(String token) throws EntityNotFoundException {
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);

        verificationTokenOptional.orElseThrow(() -> new EntityNotFoundException("Invalid token!"));
        fetchUserAndEnable(verificationTokenOptional.get());
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) throws EntityNotFoundException {
        String userName = verificationToken.getUser().getUsername();
        User   user     = userRepository.findByUsername(userName).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userName));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String authenticationToken = jwtProvider.generateToken(authenticate);

        return AuthenticationResponse.builder().authenticationToken(authenticationToken)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiredAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername()).build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());

        return AuthenticationResponse.builder().authenticationToken(token).refreshToken(refreshTokenRequest.getRefreshToken())
                .expiredAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername()).build();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new UsernameNotFoundException(
                "User name not found - " + principal.getUsername()));
    }

}
