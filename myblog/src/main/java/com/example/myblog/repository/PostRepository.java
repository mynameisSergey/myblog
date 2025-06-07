package com.example.myblog.repository;

import com.example.myblog.model.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    List<Post> getPosts(String search, int pageSize, int offset);

    int getPostsCount();

    Optional<Post> getById(Long id);

    long save(Post post);

    void updateById(Long id, Post post);

    void likeById(Long id, int likeCount);

    void deleteById(Long id);
}
