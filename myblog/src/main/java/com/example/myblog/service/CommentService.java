package com.example.myblog.service;

import com.example.myblog.model.entity.Comment;
import com.example.myblog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByPostId(Long postId){
        return commentRepository.getCommentsByPostId(postId);
    }



}
