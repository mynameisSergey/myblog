package com.example.myblog.model.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PagingParametersDto {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}
