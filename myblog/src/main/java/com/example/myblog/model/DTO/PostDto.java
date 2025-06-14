package com.example.myblog.model.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class PostDto {
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String title;
    @EqualsAndHashCode.Include
    private String text;
    private MultipartFile image;
    @EqualsAndHashCode.Include
    private String tags;
}
