package com.example.myblog.controller;

import com.example.myblog.model.DTO.PostDto;
import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.DTO.PostsWithParametersDto;
import com.example.myblog.model.entity.Post;
import com.example.myblog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @GetMapping("/")
    public String redirectPosts() {
        return "redirect:/posts";
    }

    @GetMapping
    public String getPosts(Model model,
                           @RequestParam(defaultValue = "", name = "search") String search,
                           @RequestParam(defaultValue = "10", name = "pageSize") int pageSize,
                           @RequestParam(defaultValue = "1", name = "pageNumber") int pageNumber) {
        PostsWithParametersDto posts = postService.getPosts(search, pageNumber, pageSize);
        model.addAttribute("posts", posts.getPosts());
        model.addAttribute("search", search);
        model.addAttribute("paging", posts.getPaging());
        return "posts";
    }

    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Long id, Model model) {
        PostFullDto postFullDto = postService.getPostFullDtoById(id);
        model.addAttribute("post", postFullDto);
        return "post";
    }

    @GetMapping("/add")
    public String addPostPage() {
        return "add-post";
    }

    @PostMapping
    public String addPost(@ModelAttribute("post") PostDto postDto) {
        PostFullDto postFullDto = postService.savePost(postDto);
        return "redirect:/posts/" + postFullDto.getId();
    }


}
