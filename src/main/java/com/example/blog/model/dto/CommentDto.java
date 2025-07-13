package com.example.blog.model.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentDto {
    private Long id;
    private String text;
    private Long postId;
}