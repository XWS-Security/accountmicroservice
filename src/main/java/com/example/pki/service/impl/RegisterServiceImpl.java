package com.example.pki.service.impl;

import Exceptions.BadActivationCodeException;
import Exceptions.PasswordIsNotValid;
import Exceptions.PasswordsDoNotMatch;
import Exceptions.RegistrationTimeExpiredException;
import com.example.pki.mail.AccountActivationLinkMailFormatter;
import com.example.pki.mail.MailService;
import com.example.pki.model.Role;
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
import java.sql.Timestamp;

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
        List<Role> auth = authService.findByname(user.getAdministrationRole());

        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRoles(auth);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRegistrationSentDate(new Timestamp(System.currentTimeMillis()));

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
            userRepository.delete(user);
            throw new BadActivationCodeException();
        }

        if(!isRegistrationTimeValid(user.getRegistrationSentDate())) {
            userRepository.delete(user);
            throw new RegistrationTimeExpiredException();
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

    private boolean isRegistrationTimeValid(Timestamp timestamp){
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        long milliseconds = timeNow.getTime() - timestamp.getTime();
        int seconds = (int) milliseconds / 1000;
        int minutes = (seconds % 3600) / 60;
        return minutes < 1;
    }
}
