package com.example.pki.service.impl;

import com.example.pki.exceptions.InvalidTokenRequestException;
import com.example.pki.exceptions.UserNotFoundException;
import com.example.pki.model.NistagramUser;
import com.example.pki.model.TokenOwner;
import com.example.pki.model.dto.CreateTokenOwnerDto;
import com.example.pki.model.dto.saga.FollowerMicroserviceCreateUserResponse;
import com.example.pki.repository.UserRepository;
import com.example.pki.security.TokenUtils;
import com.example.pki.service.AgentTokenService;
import com.example.pki.service.AuthorityService;
import com.example.pki.service.CertificateService;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;

@Service
public class AgentTokenServiceImpl implements AgentTokenService {
    private final TokenUtils tokenUtils;
    private final AuthorityService authorityService;
    private final UserRepository userRepository;
    private final CertificateService certificateService;

    @Value("${CAMPAIGN}")
    private String campaignMicroserviceURI;

    public AgentTokenServiceImpl(TokenUtils tokenUtils, AuthorityService authorityService, UserRepository userRepository, CertificateService certificateService) {
        this.tokenUtils = tokenUtils;
        this.authorityService = authorityService;
        this.userRepository = userRepository;
        this.certificateService = certificateService;
    }

    @Override
    public String get() throws SSLException {
        var user = getCurrentlyLoggedUser();
        if (!user.isAgent()) throw new InvalidTokenRequestException();
        TokenOwner tokenOwner = createTokenOwner(user);
        return tokenUtils.generateToken(tokenOwner.getUsername());
    }

    private TokenOwner createTokenOwner(NistagramUser user) throws SSLException {
        TokenOwner tokenOwner = new TokenOwner();
        tokenOwner.setLastPasswordResetDate(user.getLastPasswordResetDate());
        String username = Base32.random();
        tokenOwner.setNistagramUsername(username);
        tokenOwner.setRoles(authorityService.findByname(tokenOwner.getAdministrationRole()));
        userRepository.save(tokenOwner);
        createTokenOwnerInCampaignMicroservice(tokenOwner, user.getUsername());
        return tokenOwner;
    }

    private NistagramUser getCurrentlyLoggedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            throw new UserNotFoundException();
        } else {
            return (NistagramUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }

    private void createTokenOwnerInCampaignMicroservice(TokenOwner tokenOwner, String agentUsername) throws SSLException {
        CreateTokenOwnerDto userDto = new CreateTokenOwnerDto(tokenOwner.getUsername(), agentUsername);

        // Creating web client.
        WebClient client = WebClient.builder()
                .baseUrl(campaignMicroserviceURI)
                .clientConnector(new ReactorClientHttpConnector(certificateService.buildHttpClient()))
                .build();

        // Define a method.
        client.post()
                .uri("/users/token/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(userDto), CreateTokenOwnerDto.class)
                .retrieve()
                .bodyToMono(FollowerMicroserviceCreateUserResponse.class)
                .block();
    }
}
