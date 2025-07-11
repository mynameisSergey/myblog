package com.example.blog.service;

import com.example.blog.mapper.CommentMapper;
import com.example.blog.model.dto.CommentDto;
import com.example.blog.model.entity.Comment;
import com.example.blog.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<CommentDto> getPostComments(Long postId) {
        return commentMapper.toCommentsDto(commentRepository.getByPostId(postId));
    }

    @Transactional
    public Comment save(Long postId, String text) {
        CommentDto comment = CommentDto.builder()
                .text(text)
                .postId(postId)
                .build();
    return commentRepository.save(commentMapper.toComment(comment));
    }

    @Transactional
    public Comment edit(Long postId, Long id, String text) {
        CommentDto comment = CommentDto.builder()
                .postId(postId)
                .text(text)
                .id(id)
                .build();
        return commentRepository.save(commentMapper.toComment(comment));
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
