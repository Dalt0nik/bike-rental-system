package lt.psk.bikerental.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lt.psk.bikerental.DTO.User.UserInfoDTO;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    final static String userInfoPath = "/userinfo";

    private final RestTemplate restTemplate;

    @Value("${okta.oauth2.issuer}")
    private String auth0Domain;

    public UserInfoDTO getUserInfoFromAuth(Jwt token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getTokenValue());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<UserInfoDTO> response = restTemplate.exchange(
                auth0Domain + userInfoPath, HttpMethod.GET, entity, UserInfoDTO.class);
        return response.getBody();
    }

    @Transactional
    public boolean insertUser(Jwt token) {

        String auth0Id = token.getClaim("sub").toString();

        if(userRepository.findByAuth0Id(auth0Id).isPresent()) {
        return false;
        }
        
        UserInfoDTO userInfoDTO = getUserInfoFromAuth(token);
        log.info("user: {}", userInfoDTO);
//        User newUser = modelMapper.map(userInfoDTO, User.class);
//        userRepository.save(newUser);
        return true;
    }
}
