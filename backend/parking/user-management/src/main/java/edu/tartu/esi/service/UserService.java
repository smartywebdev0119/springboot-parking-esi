package edu.tartu.esi.service;

import edu.tartu.esi.UserMapper;
import edu.tartu.esi.dto.PaginatedResponseDto;
import edu.tartu.esi.dto.UserDto;
import edu.tartu.esi.exception.EmailAlreadyExistsException;
import edu.tartu.esi.exception.UserNotFoundException;
import edu.tartu.esi.model.User;
import edu.tartu.esi.repository.UserRepository;
import edu.tartu.esi.repository.UserRoleRepository;
import edu.tartu.esi.search.GenericSearchDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private UserRoleRepository roleRepository;
    private final UserMapper userMapper;

    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    public void createUser(UserDto userDto) {
        assertUserDto(userDto, "Can't create a user info when user is null");
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.info("-- createUser; Email already exists");
            throw new EmailAlreadyExistsException(format("Email %s already exists", userDto.getEmail()));
        }
        User newUser = User.builder()
                .email(userDto.getEmail())
                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userDto.getPassword()))
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .userRole(userDto.getUserRoleEnum())
                .paymentMethod(userDto.getPaymentMethod())
                .build();
        userRepository.save(newUser);

//        kafkaTemplate.send("userCreatedTopic", userDto);

        log.info("-- createUser; User {} is created", userDto.getId());
    }

    @Transactional
    public UserDto getUserById(String id) {
        log.info("-- fetchUser");
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(format("User with id = %s wasn't found", id)));
        return userMapper.toDto(user);
    }

    public PaginatedResponseDto<UserDto> getUsers(GenericSearchDto<UserDto> searchDto) {
        log.info("-- getUsers");
        Page<User> userList = userRepository.findAll(searchDto.getSpecification(), searchDto.getPageable());
        List<UserDto> userDtos = userMapper.toDtos(userList.getContent());

        return PaginatedResponseDto.<UserDto>builder()
                .page(searchDto.getPage())
                .size(userDtos.size())
                .sortingFields(Arrays.stream(searchDto.getSort())
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")))
                .sortDirection(searchDto.getDir().name())
                .data(userDtos)
                .build();
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
        log.info("-- User {} is deleted", id);
    }

    public void updateUser(String id, UserDto userDto) {
        User newUser = User.builder()
                .id(id)
                .email(userDto.getEmail())
                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userDto.getPassword()))
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .userRole(userDto.getUserRoleEnum())
                .paymentMethod(userDto.getPaymentMethod())
                .build();
        userRepository.save(newUser);
        log.info("-- User {} is updated", id);
    }

    private void assertUserDto(UserDto user, String msg) {
        if (user == null) {
            log.info("The body payload is missed");
            throw new IllegalArgumentException(msg);
        }
    }
}
