package com.example.blog;

import com.example.blog.mapper.CommentMapper;
import com.example.blog.model.dto.CommentDto;
import com.example.blog.model.entity.Comment;
import com.example.blog.model.entity.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.service.CommentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ModelCommentTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    @SneakyThrows
    void testAddComments() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(3)
                .tags("test")
                .build();
        Comment comment = Comment.builder()
                .post(post)
                .text("Про пост")
                .build();
        when(commentMapper.toComment(any(CommentDto.class))).thenReturn(comment);
        commentService.save(1L, "Про пост");
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testEditComments() {
        Post post = Post.builder()
                .id(2L)
                .title("Тестовый пост новый")
                .text("Текст")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .post(post)
                .text("Про пост")
                .build();
        when(commentMapper.toComment(any(CommentDto.class))).thenReturn(comment);

        commentService.edit(2L, 2l, "Про пост");
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testDeleteById() {
        commentService.deleteById(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }
}
