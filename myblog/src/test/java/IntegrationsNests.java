import com.example.myblog.model.DTO.PostDto;
import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.DTO.PostsWithParametersDto;
import com.example.myblog.model.entity.Comment;
import com.example.myblog.model.entity.Post;
import com.example.myblog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class IntegrationsNests extends CoreTests {

    @Test
    void testController() {
        assertNotNull(postController);
    }

    @Test
    void testGetPost() {
        Post post = getLastPost().orElse(null);
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
        PostFullDto postFullDto = postService.savePost(postDto);
        assertNotNull(postFullDto);
        assertNotNull(postFullDto.getId());
        assertEquals("Тестовый пост", postFullDto.getTitle());
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
        Post lastPost = getLastPost().orElse(null);
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
            log.info("Картинка не найдена");
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
        assertEquals(0, post.getLikes_count());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false, false})
    void testAddLikePost(boolean like) {
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        Post post = postService.getPostById(insertedPost.getId());
        postService.likePostBById(insertedPost.getId(), like);
        Post editedPost = postService.getPostById(insertedPost.getId());
        assertNotNull(post);
        assertNotNull(editedPost);
        assertEquals(like ? post.getLikes_count() + 1 : (post.getLikes_count() > 0 ? post.getLikes_count() - 1 : 0), editedPost.getLikes_count());
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
            log.info("Картинка не найдена");
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
        Post insertedPost = getLastPost().orElse(null);
        Comment insertedComments = getLastComment().orElse(null);
        Comment comment = Comment.builder()
                .id(insertedComments.getId() + 1)
                .post_id((insertedPost.getId()))
                .text("Текст")
                .build();
        commentService.save(insertedPost.getId(), comment.getText());
        List<Comment> comments = commentService.getCommentsByPostId(insertedPost.getId());
        assertTrue(comments.size()>1);
        assertTrue(comments.stream().anyMatch(commen -> "Текст".equals(comment.getText())));

    }

    @Test
    void testUpdateComments(){
        Post post = getLastPost().orElse(null);
        assertNotNull(post);
        List<Comment> comments = commentService.getCommentsByPostId(post.getId());

        Comment comment = getLastComment().orElse(null);

        commentService.save(post.getId(), "Тестовый коммент");
        comment= getLastComment().orElse(null);
        assertNotNull(comment);
        assertNotNull(comment.getId());

        final Long commentId = comment.getId();
        final String text = "Новый комментарий";

        commentService.edit(post.getId(), commentId, text);

        comments = commentService.getCommentsByPostId(post.getId());
        assertTrue(comments.stream().anyMatch(comm -> commentId.equals(comm.getId()) && text.equals(comm.getText())));
    }

    @Test
    void testDeleteComments(){
        Post post = getLastPost().orElse(null);

        Comment comment1 = Comment.builder()
                .post_id(post.getId())
                .text("Тестовый текст")
                .build();
        Comment comment2 = Comment.builder()
                .post_id(post.getId())
                .text("Еще один коммент")
                .build();

        commentService.save(post.getId(), comment1.getText());
        commentService.save(post.getId(), comment2.getText());

        Comment comment = getLastComment().orElse(null);
        PostFullDto postFullDto = postService.getPostFullDtoById(post.getId());
        assertNotNull(comment);
        commentService.deleteById(comment.getId());
        assertEquals(postFullDto.getComments().size()-1, commentService.getCommentsByPostId(postFullDto.getId()).toArray().length);

    }

    public Optional<Post> getLastPost() {
        String sql = "SELECT id, TITLE, text, tags, likes_count, IMAGE " +
                "FROM post " +
                "WHERE id = (SELECT MAX(id) FROM post)";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Post.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .text(rs.getString("text"))
                .tags(rs.getString("tags"))
                .likes_count(rs.getInt("likes_count"))
                .image(rs.getBytes("image"))
                .build()));
    }


    private Optional<Comment> getLastComment() {
        String sql = "SELECT id, post_id, text " +
                "FROM comment " +
                "WHERE id = (SELECT MAX(id) FROM comment)";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Comment.builder()
                .id(rs.getLong("id"))
                .text(rs.getString("text"))
                .post_id(rs.getLong("post_id"))
                .build()));
    }

}
