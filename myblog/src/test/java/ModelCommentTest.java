import com.example.myblog.model.entity.Comment;
import com.example.myblog.repository.CommentRepository;
import com.example.myblog.service.CommentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ModelCommentTest extends CoreTests {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeAll
    public static void setUpBeforeClass() {
        log.info("Запускаем тесты");
    }

    @AfterAll
    public static void tearDownAfterClass() {
        log.info("Все тесты проведены");
    }

    @Test
    @SneakyThrows
    void testAddComments() {
        Comment comment = Comment.builder()
                .post_id(1L)
                .text("Про пост")
                .build();
        commentService.save(1L, "Про пост");
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testEditComments() {
        Comment comment = Comment.builder()
                .id(1L)
                .post_id(1L)
                .text("Про пост")
                .build();
        commentService.edit(1L, 1l, "Про пост");
        verify(commentRepository, times(1)).updateById(1L, comment);
    }

    @Test
    void testDeleteById() {
        commentService.deleteById(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }
}
