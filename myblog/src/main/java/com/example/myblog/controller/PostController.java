package com.example.myblog.controller;

import com.example.myblog.model.DTO.PostDto;
import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.DTO.PostsWithParametersDto;
import com.example.myblog.service.CommentService;
import com.example.myblog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

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

    @GetMapping("/images/{id}")
    public byte[] getImage(@PathVariable("id") Long id) {
        return postService.getImage(id);
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable("id") Long id,
                           @RequestParam("like") boolean like) {
        postService.likePostBById(id, like);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/edit")
    public String editPostPage(@PathVariable("id") Long id, Model model) {
        PostDto postDto = postService.getPostDtoById(id);
        model.addAttribute("post", postDto);
        return "redirect:add-post";
    }

    @PostMapping("/{id}")
    public String editPost(@PathVariable("id") Long id,
                           @ModelAttribute("post") PostDto postDto) {
        postService.editPostById(id, postDto);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/comments")
    public String addComment(Model model, @PathVariable("id") Long id,
                             @RequestParam(defaultValue = "", name = "text") String text) {
        model.addAttribute("text", text);
        commentService.save(id, text);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/comments/{commentId}")
    public String editComment(Model model,
                              @PathVariable("id") Long id,
                              @PathVariable("commentId") Long commentId,
                              @RequestParam(defaultValue = "", name = "text") String text) {
        model.addAttribute("text", text);
        commentService.edit(id, commentId, text);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("id") Long id,
                                @PathVariable("commentId") Long commentId){
        commentService.deleteById(commentId);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable("id") Long id) {
        postService.deletePostById(id);
        return "redirect:/posts";
    }

}
