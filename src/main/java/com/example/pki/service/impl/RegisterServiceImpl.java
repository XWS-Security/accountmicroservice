package com.example.pki.service.impl;

import com.example.pki.exceptions.*;
import com.example.pki.mail.AccountActivationLinkMailFormatter;
import com.example.pki.mail.MailService;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.Role;
import com.example.pki.model.User;
import com.example.pki.model.dto.RegisterDto;
import com.example.pki.model.dto.saga.CreateUserOrchestratorResponse;
import com.example.pki.repository.UserRepository;
import com.example.pki.saga.createuser.CreateUserOrchestrator;
import com.example.pki.service.AuthorityService;
import com.example.pki.service.CertificateService;
import com.example.pki.service.RegisterService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.net.ssl.SSLException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;
    private final AuthorityService authService;
    private final PasswordEncoder passwordEncoder;
    private final MailService<String> mailService;
    private final CertificateService certificateService;

    @Value("${CONTENT}")
    private String contentMicroserviceURI;

    @Value("${FOLLOWER}")
    private String followerMicroserviceURI;

    @Autowired
    public RegisterServiceImpl(UserRepository userRepository, AuthorityService authService, PasswordEncoder passwordEncoder,
                               MailService<String> mailService, CertificateService certificateService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.certificateService = certificateService;
    }

    @Override
    public Mono<CreateUserOrchestratorResponse> register(RegisterDto dto, String siteURL) throws MessagingException, PasswordIsNotValid, SSLException {

        if (!dto.getPassword().equals(dto.getRepeatedPassword())) {
            throw new PasswordsDoNotMatch();
        }

        NistagramUser user = new NistagramUser();
        List<Role> auth = authService.findByname(user.getAdministrationRole());

        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRoles(auth);
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRegistrationSentDate(new Timestamp(System.currentTimeMillis()));
        user.setNistagramUsername(dto.getUsername());
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAbout(dto.getAbout());
        user.setProfilePrivate(dto.isProfilePrivate());

        String activationCode = RandomString.make(64);
        user.setActivationCode(activationCode);

        if (userExists(user.getEmail(), user.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        sendActivationLink(user, siteURL);

        var orchestrator = new CreateUserOrchestrator(getFollowerMicroserviceWebClient(), getContentMicroserviceWebClient(), userRepository);
        return orchestrator.createUser(user);
    }

    @Override
    public NistagramUser activate(String email, String activationCode) throws BadActivationCodeException {
        NistagramUser user = findByEmail(email);
        if (!user.getActivationCode().equals(activationCode)) {
            userRepository.delete(user);
            throw new BadActivationCodeException();
        }

        if (!isRegistrationTimeValid(user.getRegistrationSentDate())) {
            userRepository.delete(user);
            throw new RegistrationTimeExpiredException();
        }
        user.Enable();
        user.setActivationCode(null);
        user = this.userRepository.save(user);
        return user;
    }

    @Override
    public NistagramUser findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        } else {
            return (NistagramUser) user;
        }
    }

    @Override
    public NistagramUser findByUsername(String username) {
        User user = userRepository.findByNistagramUsername(username);
        if (user == null) {
            return null;
        } else {
            return (NistagramUser) user;
        }
    }

    @Override
    public boolean userExists(String email, String username) {
        try {
            return findByEmail(email) != null || findByUsername(username) != null;
        } catch (Exception e) {
            return true;
        }
    }

    private void sendActivationLink(NistagramUser nistagramUser, String siteUrl) throws MessagingException {
        String verifyURL = siteUrl + "/activation?code=" + nistagramUser.getActivationCode() + "&email=" + nistagramUser.getEmail();
        mailService.sendMail(nistagramUser.getEmail(), verifyURL, new AccountActivationLinkMailFormatter());
    }

    private boolean isRegistrationTimeValid(Timestamp timestamp) {
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        long milliseconds = timeNow.getTime() - timestamp.getTime();
        int seconds = (int) milliseconds / 1000;
        int minutes = (seconds % 3600) / 60;
        return minutes < 1;
    }

    private WebClient getFollowerMicroserviceWebClient() throws SSLException {
        return WebClient.builder()
                .baseUrl(followerMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();
    }

    private WebClient getContentMicroserviceWebClient() throws SSLException {
        return WebClient.builder()
                .baseUrl(contentMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();
    }
}
