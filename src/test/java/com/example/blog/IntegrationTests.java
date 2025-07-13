package com.example.blog;

import com.example.blog.mapper.PostMapper;
import com.example.blog.model.dto.CommentDto;
import com.example.blog.model.dto.PostDto;
import com.example.blog.model.dto.PostFullDto;
import com.example.blog.model.dto.PostsWithParametersDto;
import com.example.blog.model.entity.Post;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests extends CoreTests {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostMapper postMapper;

    @Test
    void testGetPost() {
        Post post = getAnyPost().orElse(null);
        assertNotNull(post);
        assertNotNull(post.getId());
        Post post1 = postService.getPostById(post.getId());
        assertNotNull(post1);
        assertNotNull(post1.getId());
        assertNotNull(post1.getTitle());
        assertNotNull(post1.getText());
    }

    @Test
    void testGetPosts() {
        PostsWithParametersDto posts = postService.getPosts(null, 1, 10);
        assertNotNull(posts);
        assertNotNull(posts.getPosts());
        assertNotNull(posts.getPaging());
        posts.getPosts().forEach(System.out::println);

    }

    @ParameterizedTest
    @ValueSource(strings = {"\tЭто тестовый пост новый с картинкой", "\tЭто тестовый \tпост новый с \tкартинкой"})
    void testGetPostDto(String text) {
        PostDto postDto = PostDto.builder()
                .title("Тестовый пост")
                .text(text)
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            postDto.setImage(multipartFile);
        } catch (IOException e) {
        }
        PostFullDto postFullDto = postService.savePost(postDto);
        assertNotNull(postFullDto);
        assertNotNull(postFullDto.getId());
        assertEquals("Тестовый пост", postFullDto.getTitle());
        assertNotNull(postFullDto.getTextPreview());

    }

    @Test
    void testAddPost() {
        PostDto postDto = PostDto.builder()
                .title("Тестовый пост")
                .text("\tЭто тестовый пост для сохранения")
                .build();
        PostFullDto postFullDto = postService.savePost(postDto);
        assertNotNull(postFullDto);
        assertNotNull(postFullDto.getId());
        assertEquals("Тестовый пост", postFullDto.getTitle());
        assertEquals("\tЭто тестовый пост для сохранения", postFullDto.getTextPreview());
        assertEquals(0, postFullDto.getLikesCount());
    }

    @Test
    void testEditPost() {
        Long id = 0L;
        Post lastPost = getAnyPost().orElse(null);
        if (lastPost != null)
            id = lastPost.getId();
        PostDto postDto = PostDto.builder()
                .id(id)
                .title("Тестовый пост для апдейта")
                .text("Тестовый пост для апдейта")
                .tags("Тег")
                .build();
        postService.editPostById(id, postDto);
        Post post = postService.getPostById(id);
        assertNotNull(postDto);
        assertNotNull(postDto.getId());
        assertEquals("Тег", post.getTags());
        assertEquals("Тестовый пост для апдейта", post.getText());
        assertEquals("Тестовый пост для апдейта", post.getTitle());
    }

    @Test
    void testUpdateEmptyPicturePost() {
        PostDto postDto = PostDto.builder()
                .title("Тестовый тайтл")
                .text("Тестовый текст")
                .tags("Тестовый тег")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            postDto.setImage(multipartFile);
        } catch (IOException e) {
        }

        PostFullDto postFullDto = postService.savePost(postDto);
        assertNotNull(postFullDto);
        assertNotNull(postFullDto.getId());

        postDto.setImage(null);
        postDto.setTitle("New");
        postService.editPostById(postFullDto.getId(), postDto);
        Post post = postService.getPostById(postFullDto.getId());
        assertNotNull(post);
        assertNotNull(post.getId());
        assertNotNull(post.getImage());
        assertEquals("New", post.getTitle());
        assertEquals("Тестовый текст", post.getText());
        assertEquals("Тестовый тег", post.getTags());
        assertEquals(0, post.getLikesCount());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false, false})
    void testAddLikePost(boolean like) {
        Post insertedPost = getAnyPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        Post post = postService.getPostById(insertedPost.getId());
        postService.likePostById(insertedPost.getId(), like);
        Post editedPost = postService.getPostById(insertedPost.getId());
        assertNotNull(post);
        assertNotNull(editedPost);
        assertEquals(like ? post.getLikesCount() + 1 : (post.getLikesCount() > 0 ? post.getLikesCount() - 1 : 0),
                editedPost.getLikesCount());
    }

    @Test
    void getImage() {
        PostDto postDto = PostDto.builder()
                .title("Тестовый тайтл")
                .text("Тестовый текст")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            postDto.setImage(multipartFile);
        } catch (IOException e) {
        }

        PostFullDto insertetPost = postService.savePost(postDto);
        byte[] image = postService.getImage(insertetPost.getId());
        assertNotNull(image);
    }

    @Test
    void testDeletePost() {
        postService.deletePostById(1L);
        assertThrows(NoSuchElementException.class, () -> postService.getPostById(1L));
    }

    @Test
    void testAddComments() {
        Post insertedPost = getAnyPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        CommentDto insertedComments = getLastComment().orElse(null);
        assertNotNull(insertedComments);
        assertNotNull(insertedComments.getId());

        CommentDto comment1 = CommentDto.builder()
                .id(insertedComments.getId() + 1)
                .postId(insertedPost.getId())
                .text("Отличный пост!")
                .build();

        commentService.save(insertedPost.getId(), comment1.getText());

        List<CommentDto> comments = commentService.getPostComments(insertedPost.getId());
        assertFalse(comments.isEmpty());
        assertTrue(comments.stream().anyMatch(comm -> "Отличный пост!".equals(comm.getText())));

    }

    @Test
    void testUpdateComments() {
        Post post = getAnyPost().orElse(null);
        assertNotNull(post);

        CommentDto comment = getLastComment().orElse(null);
        assertNotNull(comment);

        commentService.save(post.getId(), "Тестовый коммент");
        comment = getLastComment().orElse(null);
        assertNotNull(comment.getId());

        final Long commentId = comment.getId();
        final String text = "Новый комментарий";

        commentService.edit(post.getId(), commentId, text);

        List<CommentDto> comments = commentService.getPostComments(post.getId());
        assertTrue(comments.stream().anyMatch(comm -> commentId.equals(comm.getId()) && text.equals(comm.getText())));
    }

    @Test
    void testDeleteComments() {
        Post post = getAnyPost().orElse(null);
        assertNotNull(post);
        CommentDto comment1 = CommentDto.builder()
                .postId(post.getId())
                .text("Тестовый текст")
                .build();
        CommentDto comment2 = CommentDto.builder()
                .postId(post.getId())
                .text("Еще один коммент")
                .build();

        commentService.save(post.getId(), comment1.getText());
        commentService.save(post.getId(), comment2.getText());

        CommentDto comment = getLastComment().orElse(null);
        assertNotNull(comment);

        PostFullDto postFullDto = postService.getPostFullDtoById(post.getId());
        commentService.deleteById(comment.getId());
        assertEquals(postFullDto.getComments().size() - 1, commentService.getPostComments(postFullDto.getId()).toArray().length);

    }
}
