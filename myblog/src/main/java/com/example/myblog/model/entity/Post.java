package com.example.myblog.model.entity;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Post {
    private Long id;
    private String title;
    private String text;
    private String tags;
    private int likes_count;
    private byte[] image;
}