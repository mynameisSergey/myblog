package com.example.blog.model.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class PostDto {
    private Long id;
    private String title;
    private String text;
    private MultipartFile image;
    private String tags;
}
