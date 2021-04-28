package com.example.pki.service.impl;

import com.example.pki.exceptions.*;
import com.example.pki.mail.MailService;
import com.example.pki.mail.PasswordResetMailFormatter;
import com.example.pki.model.User;
import com.example.pki.model.dto.ChangePasswordDto;
import com.example.pki.model.dto.ResetPasswordDto;
import com.example.pki.repository.UserRepository;
import com.example.pki.service.PasswordResetService;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{10,20}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService<String> mailService;
    private final AuthenticationManager authenticationManager;

    public PasswordResetServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService<String> mailService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void sendPasswordResetCode(String email) throws EmailDoesNotExistException, MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new EmailDoesNotExistException();

        String code = RandomString.make(10);
        mailService.sendMail(email, code, new PasswordResetMailFormatter());
        user.setPasswordResetCode(code);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordDto passwordDto) throws BadPasswordResetCodeException, PasswordsDoNotMatch, PasswordIsNotValid, PasswordResetTriesExceededException {
        User user = userRepository.findByEmail(passwordDto.getEmail());

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
    public void changePassword(ChangePasswordDto passwordDto, String email) throws PasswordsDoNotMatch, PasswordIsNotValid, BadCredentialsException {
        User user = userRepository.findByEmail(email);
        // TODO: check old password and email
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, passwordDto.getOldPassword()));

        // TODO: validate new password
        validatePasswords(passwordDto.getNewPassword(), passwordDto.getNewPasswordRepeated());

        // TODO: change password and save user
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        user.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
    }

    private void validatePasswords(String password, String passwordRepeated) throws PasswordsDoNotMatch, PasswordIsNotValid {
        if (!password.equals(passwordRepeated)) throw new PasswordsDoNotMatch();
        if (!isPasswordSafe(password)) throw new PasswordIsNotValid();
    }

    private boolean isPasswordSafe(String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
