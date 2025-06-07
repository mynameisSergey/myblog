package com.example.myblog.controller;

import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.DTO.PostsWithParametersDto;
import com.example.myblog.model.entity.Post;
import com.example.myblog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String redirectPosts() {
        return "redirect:/posts";
    }

    @GetMapping
    public String getPosts(Model model, @RequestParam(defaultValue = "", name = "search") String search,
                           @RequestParam(defaultValue = 10, name = "pageSize") int pageSize,
                           @RequestParam(defaultValue = 1, name = "pageNumber") int pageNumber) {
        PostsWithParametersDto posts = postService.getPosts(search, pageSize, pageNumber);
        model.addAllAttributes("posts", posts.getPosts());
        model.addAttribute("search", search);
        model.addAllAttributes("paging", posts.getPaging());
        return "posts";
    }

}
