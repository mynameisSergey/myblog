package com.example.myblog.repository;

import com.example.myblog.model.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> getPosts(String search, int limit, int offset) {
        List<Post> posts = jdbcTemplate.query("select id, title, text, tags, likes_count, image from post " + "where tags like CONCAT('%', COALESCE(?, ''), '%') " + "order by id desc limit ? offset ?", (rs, rowNum) -> Post.builder().id(rs.getLong("id")).title(rs.getString("title")).text(rs.getString("text")).tags(rs.getString("tags")).likesCount(rs.getInt("likes_count")).image(rs.getBytes("image")).build(), search, limit, offset);
        log.info("Выбрано {} постов", posts.size());
        return posts;
    }

    @Override
    public int getPostsCount() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post", Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public Optional<Post> getById(Long id) {
        List<Post> posts = jdbcTemplate.query("select id, title, text, tags, likes_count, image FROM post where id = ?", (rs, rowNum) -> Post.builder().id(rs.getLong("id")).title(rs.getString("title")).text(rs.getString("text")).tags(rs.getString("tags")).likesCount(rs.getInt("likes_count")).image(rs.getBytes("image")).build(), id);
        return posts.isEmpty() ? Optional.empty() : Optional.of(posts.getFirst());
    }

    @Override
    public long save(Post post) {
        String sql = (hasImage(post)) ? "insert into post(title, text, tags, image) values(?, ?, ?, ?)" : "insert into post(title, text, tags) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setString(3, post.getTags());
            if (post.getImage() != null) {
                ps.setBytes(4, post.getImage());
            }
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0L;
    }

    @Override
    public void updateById(Long id, Post post) {
        String sql = (hasImage(post)) ? "update post set title = ?, text = ?, tags = ?, image = ? where id = ?" : "update post set title = ?, text = ?, tags = ? where id = ?";
        List<Object> params = formParams(post, id);
        jdbcTemplate.update(sql, params.toArray());
    }

    private List<Object> formParams(Post post, Long id) {
        List<Object> params = new ArrayList<>();
        params.add(post.getTitle());
        params.add(post.getText());
        params.add(post.getTags());
        if (hasImage(post)) params.add(post.getImage());
        params.add(id);
        return params;
    }

    private boolean hasImage(Post post) {
        return post.getImage() != null && post.getImage().length > 0;
    }

    @Override
    public void likeById(Long id, int likeCount) {
        jdbcTemplate.update("update post set likes_count = ? where id = ?", likeCount, id);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from post where id = ?", id);
    }

}


