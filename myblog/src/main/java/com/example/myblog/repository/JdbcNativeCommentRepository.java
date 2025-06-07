package com.example.myblog.repository;

import com.example.myblog.model.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcNativeCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return jdbcTemplate.query("SELECT id, text, post_id FROM comment WHERE post_id = ?",
                (rs, rowNum) -> Comment.builder()
                        .id(rs.getLong("id"))
                        .text(rs.getString("text"))
                        .postId(rs.getLong("post_id"))
                        .build(), postId);
    }

    @Override
    public void save(Comment comment) {
        jdbcTemplate.update("insert into comment(text, post_id) values(?, ?)",
                comment.getText(), comment.getPostId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from comment where id = ?", id);
    }

    @Override
    public void updateById(Long id, Comment comment) {
        jdbcTemplate.update("update comment set text = ?, post_id = ? where id = ?",
                comment.getText(), comment.getPostId(), id);
    }
}
