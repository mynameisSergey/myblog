package com.example.myblog.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fast {
    private Long id;
    private byte[] image;
    private String title;
    private String text;
    private String tags;
    private int likesCount;
}
