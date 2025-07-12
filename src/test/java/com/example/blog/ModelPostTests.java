package com.example.blog;

import com.example.blog.mapper.PostMapper;
import com.example.blog.model.dto.PostDto;
import com.example.blog.model.dto.PostFullDto;
import com.example.blog.model.entity.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ModelPostTests extends CoreTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private PostService postService;

    @Test
    void checkPostTableExists() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'POST'", Integer.class
        );
        assertTrue(count != null && count > 0, "Таблица POST не найдена!");
    }

    @Test
    void testGetPost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(3)
                .tags("test")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {}

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        Post testPost = postService.getPostById(1L);
        assertNotNull(testPost);
        assertNotNull(testPost.getId());
        assertEquals(post, testPost);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\tЭто тестовый пост новый с картинкой\n\tВторой абзац"})
    void testGetPostDto(String text) {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text(text)
                .build();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            post.setImage(multipartFile.getBytes());
        } catch (IOException e) { }

        PostFullDto postFullDto = PostFullDto.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text(Arrays.asList(text.split("\n")))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postMapper.toDto(any(Post.class))).thenReturn(postFullDto);
        when(commentService.getPostComments(anyLong())).thenReturn(null);

        PostFullDto testPost = postService.getPostFullDtoById(1L);
        assertNotNull(testPost);
        assertNotNull(testPost.getId());
        assertEquals(postFullDto, testPost);
    }

    @Test
    void testAddPost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый скартинкой")
                .likesCount(0)
                .tags("test")
                .build();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {}

        PostDto postDto = PostDto.builder()
                .title("Тестовый пост")
                .text("\tЭто тестовыйпост для сохранения")
                .tags("test")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            postDto.setImage(multipartFile);
        } catch (IOException e) {}
        PostFullDto postFullDto = PostFullDto.builder()
                .id(1L)
                .title("Тестовый пост")
                .text(List.of("\tЭто тестовый пост для сохранения"))
                .tags(List.of("test"))
                .likesCount(0)
                .imagePath("/image/1")
                .build();

        when(postMapper.toPost(any(PostDto.class))).thenReturn(post);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(commentRepository.getByPostId(anyLong())).thenReturn(new ArrayList<>());
        when(postMapper.toDto(any(Post.class))).thenReturn(postFullDto);

        PostFullDto postFullDto1 = postService.savePost(postDto);
        assertNotNull(postFullDto1);
        assertNotNull(postFullDto1.getId());
    }

    @Test
    void testEditPost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(0)
                .tags("test")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {}

        PostDto postDto = PostDto.builder()
                .id(1L)
                .title("Тестовый пост для изменения полей")
                .text("\tЭто тестовый пост 1\n\tЕще один абзац")
                .tags("test")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            postDto.setImage(multipartFile);
        } catch (IOException e) {}

        when(postMapper.toPost(any(PostDto.class))).thenReturn(post);
        postService.editPostById(1L, postDto);
        verify(postRepository, times(1)).editByIdWithoutImage(1L,
                "Тестовый пост для изменения полей", "\tЭто тестовый пост 1\n\tЕще один абзац","test");
    }

    @Test
    void testAddLikePost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("Тестовый текст")
                .likesCount(3)
                .tags("Тестовый тег")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {}

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        postService.likePostById(1L, true);
        verify(postRepository, times(1)).likeById(1L, 4);
    }

    @Test
    void testDislikePost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("Тестовый текст")
                .likesCount(3)
                .tags("Тестовый тег")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {}

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        postService.likePostById(1L, false);
        verify(postRepository, times(1)).likeById(1L, 2);
    }

    @Test
    void testGetImage() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("Тестовый текст")
                .likesCount(3)
                .tags("Тестовый тег")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {}

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        byte[] image1 = postService.getImage(1L);
        assertNotNull(image1);
    }

    @Test
    void testDeletePost() {

        postService.deletePostById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

}