package com.example.myblog.repository;

import com.example.myblog.model.entity.Post;

import java.util.List;

public interface PostRepository {

    List<Post> getPosts(String search, int pageSize, int offset);
}
