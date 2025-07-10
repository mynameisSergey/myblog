package com.example.blog.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ModelMapper modelMapper;

    public CommentDto toCommentDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    public List<CommentDto> toCommentsDto(List<Comment> comments) {
        return comments.stream().map(this::toCommentDto).toList();
    }

    public Comment toComment(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }
}
