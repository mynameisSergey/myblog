import com.example.myblog.mapper.PostMapper;
import com.example.myblog.model.DTO.PostDto;
import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.entity.Post;
import com.example.myblog.repository.CommentRepository;
import com.example.myblog.repository.PostRepository;
import com.example.myblog.service.CommentService;
import com.example.myblog.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
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
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ModelPostTests extends CoreTests {

    @Autowired
    private ModelMapper modelMapper;

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

    @Autowired
    private Environment env;

    @BeforeAll
    public void setUpBeforeClass() {
        log.info("Запускаем тесты");
    }

    @AfterAll
    public void tearDownAfterClass() {
        log.info("Завершили запуск тестов");
    }

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
                .likes_count(3)
                .tags("test")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }

        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));

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
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }
        PostFullDto postFullDto = PostFullDto.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text(Arrays.asList(text.split("\n")))
                .build();

        when(postRepository.getById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postFullDto);
        when((commentService.getCommentsByPostId(1L))).thenReturn(null);

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
                .likes_count(0)
                .tags("test")
                .build();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());
            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }

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
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }
        PostFullDto postFullDto = PostFullDto.builder()
                .id(1L)
                .title("Тестовый пост")
                .text(List.of("\tЭто тестовый пост для сохранения"))
                .tags(List.of("test"))
                .likesCount(0)
                .imagePath("/image/1")
                .build();

        when(postMapper.toPost(any(PostDto.class))).thenReturn(post);
        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(1L);
        when(commentRepository.getCommentsByPostId(anyLong())).thenReturn(new ArrayList<>());
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
                .likes_count(0)
                .tags("test")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }

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
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }
        when(postMapper.toPost(any(PostDto.class))).thenReturn(post);
        postService.editPostById(1L, postDto);
        verify(postRepository, times(1)).updateById(1L, post);
    }

    @Test
    void testAddLikePost() {
        Post post = Post.builder()
                .id(1L)
                .text("Тестовый текст")
                .likes_count(3)
                .tags("Тестовый тег")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }
        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));
        postService.likePostBById(1L, true);
        verify(postRepository, times(1)).likeById(1L, 4);
    }

    @Test
    void testADisLikePost() {
        Post post = Post.builder()
                .id(1L)
                .text("Тестовый текст")
                .likes_count(3)
                .tags("Тестовый тег")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }
        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));
        postService.likePostBById(1L, false);
        verify(postRepository, times(1)).likeById(1L, 2);
    }

    @Test
    void testGetImage() {
        Post post = Post.builder()
                .id(1L)
                .text("Тестовый текст")
                .likes_count(3)
                .tags("Тестовый тег")
                .build();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("myblogdb.png");
            assertNotNull(is, "Файл myblogdb.png не найден в resources!");
            MultipartFile multipartFile = new MockMultipartFile("myblogdb.png", is.readAllBytes());

            post.setImage(multipartFile.getBytes());
        } catch (IOException e) {
            log.info("Картинка не найдена");
        }

        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));

        byte[] image1 = postService.getImage(1L);
        assertNotNull(image1);
    }

    @Test
    void testDeletePost() {

        postService.deletePostById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

}