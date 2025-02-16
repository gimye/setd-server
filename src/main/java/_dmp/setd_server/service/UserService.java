package _dmp.setd_server.service;

import _dmp.setd_server.dto.UserResponseDTO;
import _dmp.setd_server.entity.Tag;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.repository.TagRepository;
import _dmp.setd_server.repository.UserRepository;
import _dmp.setd_server.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TagRepository tagRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public User registerUser(String username, String password, String nickname) {
        if(userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username already exist");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);

        final User savedUser = userRepository.save(user);

        // 시스템 사용자의 기본 태그 조회
        List<Tag> defaultTags = tagRepository.findByUserUsername("system");

        // 새 사용자를 위해 기본 태그를 복사
        List<Tag> newTags = defaultTags.stream()
                .map(defaultTag -> {
                    Tag newTag = new Tag();
                    newTag.setUser(savedUser);
                    newTag.setName(defaultTag.getName());
                    return newTag;
                })
                .collect(Collectors.toList());

        tagRepository.saveAll(newTags);

        return savedUser;
    }


    public User loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자 삭제 (연관된 태그, todo, finance_entries 함께 삭제됨)
        userRepository.delete(user);
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
