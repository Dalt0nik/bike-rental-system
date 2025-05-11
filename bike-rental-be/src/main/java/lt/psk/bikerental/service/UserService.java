package lt.psk.bikerental.service;

import lombok.AllArgsConstructor;
import lt.psk.bikerental.repository.UserRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public boolean insertUser() {
        return true;
    }

}
