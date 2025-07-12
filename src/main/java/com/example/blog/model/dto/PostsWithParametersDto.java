package com.example.blog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class PostsWithParametersDto {
    List<PostFullDto> posts;
    private String search;
    private PagingParametersDto paging;
}
