package com.example.blog.model.dto;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode
public class PagingParametersDto {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}
