package _dmp.setd_server.service;

import _dmp.setd_server.dto.TagRequest;
import _dmp.setd_server.dto.TagResponse;
import _dmp.setd_server.entity.Tag;
import _dmp.setd_server.entity.User;
import _dmp.setd_server.repository.TagRepository;
import _dmp.setd_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional
    public TagResponse createTag(String username, TagRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (tagRepository.existsByNameAndUserUsername(request.getName(), username)) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }

        Tag tag = new Tag();
        tag.setUser(user);
        tag.setName(request.getName());

        Tag savedTag = tagRepository.save(tag);
        return TagResponse.from(savedTag);
    }

    public List<TagResponse> getAllTags(String username) {
        return tagRepository.findByUserUsername(username).stream()
                .map(TagResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TagResponse updateTag(String username, Long tagId, TagRequest request) {
        Tag tag = tagRepository.findByIdAndUserUsername(tagId, username)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        tag.setName(request.getName());
        Tag updatedTag = tagRepository.save(tag);
        return TagResponse.from(updatedTag);
    }

    @Transactional
    public void deleteTag(String username, Long tagId) {
        Tag tag = tagRepository.findByIdAndUserUsername(tagId, username)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        tagRepository.delete(tag);
    }
}
