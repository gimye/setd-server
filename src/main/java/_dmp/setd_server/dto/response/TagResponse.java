package _dmp.setd_server.dto.response;

import _dmp.setd_server.entity.Tag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponse {
    private Long id;
    private String name;

    public static TagResponse from(Tag tag) {
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        return response;
    }
}
