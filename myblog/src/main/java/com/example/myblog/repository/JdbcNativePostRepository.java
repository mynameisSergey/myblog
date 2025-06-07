package com.example.myblog.repository;

import com.example.myblog.model.DTO.PostsWithParametersDto;
import com.example.myblog.model.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Post> getPosts(String search, int limit, int offset) {
        List<Post> posts = jdbcTemplate.query(
                "select id, title, text, tags, likes_count, image from post " +
                        "where tags like '%' || coalesce(?, '') || '%' " +
                        "order by id desc limit ? offset ?",
                (rs, rowNum) -> Post.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .text(rs.getString("text"))
                        .tags(rs.getString("tags"))
                        .likesCount(rs.getInt("likes_count"))
                        .image(rs.getBytes("image"))
                        .build(),
                search, limit, offset
        );
        log.info("Выбрано {} постов", posts.size());
        return posts;
    }

}
