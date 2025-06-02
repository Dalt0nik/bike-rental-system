package lt.psk.bikerental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lt.psk.bikerental.DTO.Booking.BookingDTO;
import lt.psk.bikerental.DTO.Trip.TripDTO;
import lt.psk.bikerental.DTO.User.UserInfoDTO;
import lt.psk.bikerental.DTO.User.UserStateDTO;
import lt.psk.bikerental.entity.User;
import lt.psk.bikerental.repository.*;
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

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;

    final static String userInfoPath = "/userinfo";

    private final RestTemplate restTemplate;
    private final BikeRepository bikeRepository;
    private final BikeStationRepository bikeStationRepository;
    private final BikeService bikeService;

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
        User newUser = modelMapper.map(userInfoDTO, User.class);
        userRepository.save(newUser);
        return true;
    }
    public UserStateDTO getUserStatus(Jwt token) {
        Instant now = Instant.now();

        String auth0Id = token.getClaim("sub");
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        BookingDTO bookingDTO = bookingRepository
                .findFirstByUserIdAndStartTimeBeforeAndFinishTimeAfter(user.getId(), now, now)
                .map(b -> modelMapper.map(b, BookingDTO.class))
                .orElse(null);

        TripDTO tripDTO = tripRepository
                .findTopByUserAndFinishTimeIsNull(user)
                .map(t -> modelMapper.map(t, TripDTO.class))
                .orElse(null);

        return UserStateDTO.builder()
                .id(user.getId())
                .booking(bookingDTO)
                .trip(tripDTO)
                .build();
    }
    public UUID getUserIdFromJwt(Jwt jwt) {
        String auth0Id = jwt.getSubject();
        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getId();
    }

}
