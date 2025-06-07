package com.example.myblog.repository;

import com.example.myblog.model.entity.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getCommentsByPostId(Long postId);

    void save(Comment comment);

    void deleteById(Long id);

    void updateById(Long id, Comment comment);
}
