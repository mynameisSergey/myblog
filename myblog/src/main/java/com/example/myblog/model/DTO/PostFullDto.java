package com.example.myblog.model.DTO;

import com.example.myblog.model.entity.Comment;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return text.getFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PostFullDto that = (PostFullDto) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, text);
    }
}
