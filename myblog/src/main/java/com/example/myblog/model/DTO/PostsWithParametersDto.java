package com.example.myblog.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class PostsWithParametersDto {
    List<PostFullDto> posts;
    private String search;
    private PagingParametersDto paging;
}
