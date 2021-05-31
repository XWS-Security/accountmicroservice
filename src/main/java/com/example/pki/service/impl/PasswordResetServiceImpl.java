package com.example.pki.service.impl;

import com.example.pki.exceptions.*;
import com.example.pki.mail.MailService;
import com.example.pki.mail.PasswordResetMailFormatter;
import com.example.pki.model.User;
import com.example.pki.model.dto.ChangePasswordDto;
import com.example.pki.model.dto.ResetPasswordDto;
import com.example.pki.model.dto.UserTokenState;
import com.example.pki.repository.UserRepository;
import com.example.pki.security.TokenUtils;
import com.example.pki.service.PasswordResetService;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
//    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{10,20}$";
//    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService<String> mailService;
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;

    public PasswordResetServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService<String> mailService, AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void sendPasswordResetCode(String email) throws EmailDoesNotExistException, MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new EmailDoesNotExistException();

        if (email.contains("<") || email.contains(">")) {
            throw new InvalidCharacterException();
        }

        String code = RandomString.make(10);
        mailService.sendMail(email, code, new PasswordResetMailFormatter());
        user.setPasswordResetCode(code);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordDto passwordDto) throws BadPasswordResetCodeException, PasswordsDoNotMatch, PasswordIsNotValid, PasswordResetTriesExceededException {
        User user = userRepository.findByEmail(passwordDto.getEmail());

//        if (passwordDto.getEmail().contains("<") || passwordDto.getEmail().contains(">")
//                || passwordDto.getCode().contains("<") || passwordDto.getCode().contains(">")
//                || passwordDto.getNewPassword().contains("<") || passwordDto.getNewPassword().contains(">")
//                || passwordDto.getNewPasswordRepeated().contains("<") || passwordDto.getNewPasswordRepeated().contains(">")) {
//            throw new InvalidCharacterException();
//        }

        if (!passwordDto.getCode().equals(user.getPasswordResetCode())) {
            user.incrementPasswordResetFailed();
            if (user.getPasswordResetFailed() >= 3) {
                user.resetPasswordResetCode();
                userRepository.save(user);
                throw new PasswordResetTriesExceededException();
            }
            userRepository.save(user);
            throw new BadPasswordResetCodeException();
        }

        validatePasswords(passwordDto.getNewPassword(), passwordDto.getNewPasswordRepeated());

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        user.resetPasswordResetCode();
        user.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
    }

    @Override
    public UserTokenState changePassword(ChangePasswordDto passwordDto, String email) throws PasswordsDoNotMatch, PasswordIsNotValid, BadCredentialsException {
//        if (passwordDto.getOldPassword().contains("<") || passwordDto.getOldPassword().contains(">")
//                || passwordDto.getNewPassword().contains("<") || passwordDto.getNewPassword().contains(">")
//                || passwordDto.getNewPasswordRepeated().contains("<") || passwordDto.getNewPasswordRepeated().contains(">")) {
//            throw new InvalidCharacterException();
//        }

        User user = userRepository.findByEmail(email);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, passwordDto.getOldPassword()));

        validatePasswords(passwordDto.getNewPassword(), passwordDto.getNewPasswordRepeated());

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        user.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        return authenticateUser(email, passwordDto.getNewPassword());
    }

    private void validatePasswords(String password, String passwordRepeated) throws PasswordsDoNotMatch, PasswordIsNotValid {
        if (!password.equals(passwordRepeated)) throw new PasswordsDoNotMatch();
//        if (!isPasswordSafe(password)) throw new PasswordIsNotValid();
    }

//    private boolean isPasswordSafe(String password) {
//        Matcher matcher = pattern.matcher(password);
//        return matcher.matches();
//    }

    private UserTokenState authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        String userType = user.getClass().getSimpleName();
        String accessToken = tokenUtils.generateToken(username);
        int accessExpiresIn = tokenUtils.getExpiredIn();
        return new UserTokenState(userType, accessToken, accessExpiresIn);
    }
}
