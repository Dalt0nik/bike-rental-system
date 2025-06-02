package lt.psk.bikerental.config.mapping;

import lt.psk.bikerental.DTO.User.UserInfoDTO;
import lt.psk.bikerental.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserMapping {
    public UserMapping(ModelMapper modelMapper) {
        modelMapper
                .createTypeMap(UserInfoDTO.class, User.class)
                .addMapping(UserInfoDTO::getSub,    User::setAuth0Id)
                .addMapping(UserInfoDTO::getName,   User::setFullName);
    }
}