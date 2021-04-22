package com.example.pki.service.impl;

import Exceptions.BadActivationCodeException;
import Exceptions.PasswordIsNotValid;
import Exceptions.PasswordsDoNotMatch;
import com.example.pki.mail.AccountActivationLinkMailFormatter;
import com.example.pki.mail.MailService;
import com.example.pki.model.Authority;
import com.example.pki.model.InstagramUser;
import com.example.pki.model.User;
import com.example.pki.model.dto.RegisterDto;
import com.example.pki.repository.UserRepository;
import com.example.pki.service.AuthorityService;
import com.example.pki.service.RegisterService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final AuthorityService authService;
    private final PasswordEncoder passwordEncoder;
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{10,20}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    private final MailService<String> mailService;

    @Autowired
    public RegisterServiceImpl(UserRepository userRepository,
                               AuthorityService authService,
                               PasswordEncoder passwordEncoder,
                               MailService<String> mailService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public InstagramUser register(RegisterDto dto, String siteURL) throws MessagingException, PasswordIsNotValid {

        if (!isPasswordValid(dto.getPassword())) {
            throw new PasswordIsNotValid();
        }

        if (!dto.getPassword().equals(dto.getRepeatedPassword())) {
            throw new PasswordsDoNotMatch();
        }

        InstagramUser user = new InstagramUser();
        List<Authority> auth = authService.findByname(user.getAdministrationRole());

        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setAuthorities(auth);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        String activationCode = RandomString.make(64);
        user.setActivationCode(activationCode);

        if (userExists(user.getEmail())) {
            throw new RuntimeException();
        }

        user = userRepository.save(user);
        sendActivationLink(user, siteURL);
        return user;
    }

    @Override
    public InstagramUser activate(String email, String activationCode) throws BadActivationCodeException {
        InstagramUser user = findByEmail(email);
        if (!user.getActivationCode().equals(activationCode)) {
            throw new BadActivationCodeException();
        }
        user.Enable();
        user.setActivationCode(null);
        user = this.userRepository.save(user);
        return user;
    }

    @Override
    public InstagramUser findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        } else {
            return (InstagramUser) user;
        }
    }

    @Override
    public boolean userExists(String email) {
        try {
            return findByEmail(email) != null;
        } catch (Exception e) {
            return true;
        }
    }

    private void sendActivationLink(InstagramUser instagramUser, String siteUrl) throws MessagingException {
        String verifyURL = siteUrl + "/activation?code=" + instagramUser.getActivationCode() + "&email=" + instagramUser.getEmail();
        mailService.sendMail(instagramUser.getEmail(), verifyURL, new AccountActivationLinkMailFormatter());
    }

    private boolean isPasswordValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
