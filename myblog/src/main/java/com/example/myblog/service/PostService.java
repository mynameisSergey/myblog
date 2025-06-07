package com.example.myblog.service;

import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.DTO.PostsWithParametersDto;
import com.example.myblog.model.entity.Post;
import com.example.myblog.repository.JdbcNativePostRepository;
import com.example.myblog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        postRepository = postRepository;
    }

    public PostsWithParametersDto getPosts(String search, int pageNumber, int pageSize){
        List<Post> posts = postRepository.getPosts(search, pageSize, (pageNumber-1) * pageSize);
        List<PostFullDto> postFullDtos = p

    }


}
