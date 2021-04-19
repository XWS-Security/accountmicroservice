package com.example.pki.service.impl;

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

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final AuthorityService authService;
    private final PasswordEncoder passwordEncoder;

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
    public InstagramUser register(RegisterDto dto, String siteURL) throws MessagingException {
        InstagramUser user = new InstagramUser();
        List<Authority> auth = authService.findByname(user.getAdministrationRole());

        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setAuthorities(auth);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        String activationCode = RandomString.make(64);
        user.setActivationCode(activationCode);
        /*if(userExists(user.getEmail())){
            throw new RuntimeException();
        }*/
        user = userRepository.save(user);
       // sendActivationLink(user, siteURL);
        return user;
    }

    @Override
    public InstagramUser activate(String email, String activationCode) {
        InstagramUser patient = findByEmail(email);
        /*if (!patient.getActivationCode().equals(activationCode)) {
            throw new BadActivationCodeException();
        }*/
        patient.Enable();
        patient.setActivationCode(null);
        patient = this.userRepository.save(patient);
        return patient;
    }

    @Override
    public InstagramUser findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        /*if (!user.getClass().equals(Patient.class)) {
            throw new NotAPatientException();
        }*/ else {
            return (InstagramUser) user;
        }
    }

    @Override
    public boolean userExists(String email) {
        /*try {
            return findByEmail(email) != null;
        } catch (NotAPatientException e) {
            return true;
        }*/
        return false;
    }

    private void sendActivationLink(InstagramUser instagramUser, String siteUrl) throws MessagingException {
        String verifyURL = siteUrl + "/activation?code=" + instagramUser.getActivationCode() + "&email=" + instagramUser.getEmail();
        mailService.sendMail(instagramUser.getEmail(), verifyURL, new AccountActivationLinkMailFormatter());
    }

}
