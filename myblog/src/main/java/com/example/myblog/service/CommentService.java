package com.example.myblog.service;

import com.example.myblog.model.entity.Comment;
import com.example.myblog.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.getCommentsByPostId(postId);
    }

    @Transactional
    public void save(Long id, String text) {
        Comment comment = Comment.builder()
                .text(text)
                .postId(id)
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void edit(Long postId, Long commentId, String text) {
        Comment comment = Comment.builder()
                .postId(postId)
                .text(text)
                .id(commentId)
                .build();
        commentRepository.updateById(commentId, comment);

    }

    @Transactional
    public void deleteById(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
