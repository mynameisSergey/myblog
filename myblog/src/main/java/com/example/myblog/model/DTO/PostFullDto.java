package com.example.myblog.model.DTO;

import com.example.myblog.model.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFullDto {
    private Long id;
    private String title;
    private List<String> text;
    private String imagePath;
    private int likesCount;
    private List<String> tags;
    private List<Comment> comments  = new ArrayList<>();
    public String getTextPreview() {
        if (text == null || text.isEmpty()) return null;
        return text.get(0);
    }
}
