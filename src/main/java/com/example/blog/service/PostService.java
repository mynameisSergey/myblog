package com.example.blog.service;

import com.example.blog.mapper.PostMapper;
import com.example.blog.model.dto.PagingParametersDto;
import com.example.blog.model.dto.PostDto;
import com.example.blog.model.dto.PostFullDto;
import com.example.blog.model.dto.PostsWithParametersDto;
import com.example.blog.model.entity.Post;
import com.example.blog.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostsWithParametersDto getPosts(String search, int pageNumber, int pageSize) {
        final Pageable page = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<Post> posts;
        if (search != null && !search.isBlank())
            posts = postRepository.getPostsByTagsLike(search, page);
        else
            posts = postRepository.findAll(page);
        List<PostFullDto> postsDto = postMapper.toListDto(posts);

        PagingParametersDto pagingParametersDto = PagingParametersDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .hasPrevious(pageNumber > 1)
                .hasNext(pageNumber < Math.ceilDiv(postRepository.count(), pageSize))
                .build();
        return new PostsWithParametersDto(postsDto, search, pagingParametersDto);
    }

    @Transactional
    public PostFullDto savePost(PostDto post) {
        return postMapper.toDto((postRepository.save(postMapper.toPost(post))));
    }

    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public void editPostById(Long id, PostDto post) {
        if (post.getId() != null) {
            if (post.getImage() == null || post.getImage().isEmpty())
                postRepository.editByIdWithoutImage(id, post.getTitle(), post.getText(), post.getTags());
            else {
                postRepository.save(postMapper.toPost(post, getPostById(id).getLikesCount()));
            }
        }
    }

    @Transactional
    public  void likePostById(Long id, boolean like) {
        int currentLikesCount = 0;
        Post post = getPostById(id);
        if(post != null)
            currentLikesCount = post.getLikesCount();

        postRepository.likeById(id, (like ? currentLikesCount+1 : (currentLikesCount > 0 ? currentLikesCount - 1 : 0)));
    }

    public  Post getPostById(Long id) {
        return  postRepository.findById(id).orElse(new Post());
    }

    public  byte[] getImage(Long id) {
        return postRepository.findById(id).orElse(new Post()).getImage();
    }

    public PostFullDto getPostFullDtoById(Long id) {
        return postMapper.toDto(getPostById(id));
    }

    public PostDto getPostDtoById(Long id) {
        return postMapper.toPostDto(getPostById(id));
    }
}
