package com.example.myblog.model.DTO;

import java.util.List;

public class PostsWithParametersDto {
    List<PostFullDto> posts;
    private String search;
    private PagingParametersDto paging;
}
