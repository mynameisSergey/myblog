package com.example.myblog.mapper;

import com.example.myblog.model.DTO.PostDto;
import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostMapper {

    private final ModelMapper mapper;

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
        return dto;
    }

    public List<PostFullDto> toListDto(List<Post> posts) {
        return posts.stream().map(this::toDto).toList();
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

    public PostDto toPostDto(Post post){
        return mapper.map(post, PostDto.class);
    }

}
