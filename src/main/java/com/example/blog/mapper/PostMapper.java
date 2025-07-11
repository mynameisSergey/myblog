package com.example.blog.mapper;

import com.example.blog.model.dto.PostDto;
import com.example.blog.model.dto.PostFullDto;
import com.example.blog.model.entity.Post;
import com.example.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PostMapper {

    private final ModelMapper mapper;
    private final CommentService commentService;

    @Value("${image.path}")
    private String imagePath;

    public PostFullDto toDto(Post entity) {
        if (entity == null) return null;
        PostFullDto dto = mapper.map(entity, PostFullDto.class);

        dto.setText(
                Optional.ofNullable(entity.getText())
                        .filter(text -> !text.isBlank())
                        .map(text -> Arrays.stream(text.split("\n")).toList())
                        .orElse(List.of())
        );

        dto.setTags(
                Optional.ofNullable(entity.getTags())
                        .filter(tags -> !tags.isBlank())
                        .map(tags -> Arrays.stream(tags.split(",\s*|\s+")).toList())
                        .orElse(List.of())
        );

        dto.setImagePath(imagePath + dto.getId());
        dto.setComments(commentService.getPostComments(entity.getId()));
        return dto;
    }

    public List<PostFullDto> toListDto(Page<Post> entities) {
        return entities.stream().map(this::toDto).toList();
    }


    public Post toPost(PostDto postDto) {
        if (postDto == null)
            return null;

        Post post = mapper.map(postDto, Post.class);
        try {

            if (postDto.getImage() != null && !postDto.getImage().isEmpty())
                post.setImage(postDto.getImage().getBytes());
        } catch (IOException e) {
            return post;
        }
        return post;
    }

    public Post toPost(PostDto dto, int likesCount) {
        Post post = toPost(dto);
        if (likesCount != 0) post.setLikesCount(likesCount);
        return post;
    }

    public PostDto toPostDto(Post post) {
        return mapper.map(post, PostDto.class);
    }

}