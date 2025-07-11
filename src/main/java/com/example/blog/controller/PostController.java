package com.example.blog.controller;

import com.example.blog.model.dto.PostDto;
import com.example.blog.model.dto.PostFullDto;
import com.example.blog.model.dto.PostsWithParametersDto;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
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


    /*
   GET "posts" - список постов на странице ленты постов
   Параметры: search - строка с поиском по тегу поста (по умолчанию, пустая строка - все посты)
              pageSize - максимальное число постов на странице (по умолчанию, 10)
              pageNumber - номер текущей страницы (по умолчанию, 1)
   Возвращает: шаблон "posts.html"
   используется модель для заполнения шаблона:
              "posts" - List<Post> - список постов (id, title, text, imagePath, likesCount, comments)
              "search" - строка поиска (по умолчанию, пустая строка - все посты)
              "paging":
              "pageNumber" - номер текущей страницы (по умолчанию, 1)
              "pageSize" - максимальное число постов на странице (по умолчанию, 10)
              "hasNext" - можно ли пролистнуть вперед
              "hasPrevious" - можно ли пролистнуть назад
    */
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

    /*
 GET "/posts/{id}" - страница с постом
 Возвращает: шаблон "post.html"
 используется модель для заполнения шаблона: "post" - модель поста (id, title, text, imagePath, likesCount, comments)
 */
    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Long id, Model model) {
        PostFullDto postFullDto = postService.getPostFullDtoById(id);
        model.addAttribute("post", postFullDto);
        return "post";
    }

    /*
       GET "/posts/add" - страница добавления поста
       Возвращает: шаблон "add-post.html"
       */
    @GetMapping("/add")
    public String addPostPage() {
        return "add-post";
    }

    /*
   POST "/posts" - добавление поста
   Принимает: "multipart/form-data"
   Параметры: "title" - название поста
                 "text" - текст поста
                 "image" - файл картинки поста (класс MultipartFile)
                 "tags" - список тегов поста (по умолчанию, пустая строка)
   Возвращает: редирект на созданный "/posts/{id}"
    */
    @PostMapping
    public String addPost(@ModelAttribute("post") PostDto postDto) {
        PostFullDto postFullDto = postService.savePost(postDto);
        return "redirect:/posts/" + postFullDto.getId();
    }

    /*
    GET "/image/{id}" -эндпоинт, возвращающий набор байт картинки поста
    Параметры: "id" - идентификатор поста
    */
    @GetMapping("/images/{id}")
    @ResponseBody
    public byte[] getImage(@PathVariable("id") Long id) {
        return postService.getImage(id);
    }

    /*
        POST "/posts/{id}/like" - увеличение/уменьшение числа лайков поста
        Параметры: "id" - идентификатор поста, "like" - если true, то +1 лайк, если "false", то -1 лайк
        Возвращает: редирект на "/posts/{id}"
         */
    @PostMapping("/{id}/like")
    public String likePost(@PathVariable("id") Long id,
                           @RequestParam("like") boolean like) {
        postService.likePostBById(id, like);
        return "redirect:/posts/" + id;
    }

    /*
       POST "/posts/{id}/edit" - страница редактирования поста
       Параметры: "id" - идентификатор поста
       Возвращает: редирект на форму редактирования поста "add-post.html"
       используется модель для заполнения шаблона: "post" - модель поста (id, title, text, imagePath, likesCount, comments)
        */
    @PostMapping("/{id}/edit")
    public String editPostPage(@PathVariable("id") Long id, Model model) {
        PostDto postDto = postService.getPostDtoById(id);
        model.addAttribute("post", postDto);
        return "redirect:add-post";
    }

    /*
        и) POST "/posts/{id}" - редактирование поста
        Принимает:
                "multipart/form-data"
        Параметры:
                "id" - идентификатор поста
                   "title" - название поста
                   "text" - текст поста
                   "image" - файл картинки поста (класс MultipartFile, может быть null - значит, остается прежним)
                   "tags" - список тегов поста (по умолчанию, пустая строка)
        Возвращает:
        редирект на отредактированный "/posts/{id}"
         */
    @PostMapping("/{id}")
    public String editPost(@PathVariable("id") Long id,
                           @ModelAttribute("post") PostDto postDto) {
        postService.editPostById(id, postDto);
        return "redirect:/posts/" + id;
    }

    /*
   POST "/posts/{id}/comments" - эндпоинт добавления комментария к посту
   Параметры: "id" - идентификатор поста, "text" - текст комментария
   Возвращает: редирект на "/posts/{id}"
   */
    @PostMapping("/{id}/comments")
    public String addComment(Model model, @PathVariable("id") Long id,
                             @RequestParam(defaultValue = "", name = "text") String text) {
        model.addAttribute("text", text);
        commentService.save(id, text);
        return "redirect:/posts/" + id;
    }

    /*
   POST "/posts/{id}/comments/{commentId}" - эндпоинт редактирования комментария
   Параметры: "id" - идентификатор поста, "commentId" - идентификатор комментария, "text" - текст комментария
   Возвращает: редирект на "/posts/{id}"
   */
    @PostMapping("/{id}/comments/{commentId}")
    public String editComment(Model model,
                              @PathVariable("id") Long id,
                              @PathVariable("commentId") Long commentId,
                              @RequestParam(defaultValue = "", name = "text") String text) {
        model.addAttribute("text", text);
        commentService.edit(id, commentId, text);
        return "redirect:/posts/" + id;
    }

    /*
        POST "/posts/{id}/comments/{commentId}/delete" - эндпоинт удаления комментария
        Параметры: "id" - идентификатор поста, "commentId" - идентификатор комментария
        Возвращает: редирект на "/posts/{id}"
        */
    @PostMapping("/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("id") Long id,
                                @PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);
        return "redirect:/posts/" + id;
    }

    /*
     POST "/posts/{id}/delete" - эндпоинт удаления поста
     Параметры: "id" - идентификатор поста
     Возвращает: редирект на "/posts"
      */
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable("id") Long id) {
        postService.deletePostById(id);
        return "redirect:/posts";
    }

}

