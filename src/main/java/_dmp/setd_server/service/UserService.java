package _dmp.setd_server.service;

import _dmp.setd_server.dto.UserResponseDTO;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.repository.UserRepository;
import _dmp.setd_server.util.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password, String nickname) {
        if(userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username already exist");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserResponseDTO updateNickname(String username, String newNickname) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        user.setNickname(newNickname);
        User updatedUser = userRepository.save(user);
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(updatedUser.getId());
        dto.setUsername(updatedUser.getUsername());
        dto.setNickname(updatedUser.getNickname());

        return dto;
    }

    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    public UserResponseDTO getUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());

        return dto;
    }
}
