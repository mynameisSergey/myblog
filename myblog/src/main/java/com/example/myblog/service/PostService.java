package com.example.myblog.service;

import com.example.myblog.mapper.PostMapper;
import com.example.myblog.model.DTO.PagingParametersDto;
import com.example.myblog.model.DTO.PostDto;
import com.example.myblog.model.DTO.PostFullDto;
import com.example.myblog.model.DTO.PostsWithParametersDto;
import com.example.myblog.model.entity.Post;
import com.example.myblog.repository.JdbcNativePostRepository;
import com.example.myblog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostMapper postMapper;

    @Transactional
    public PostsWithParametersDto getPosts(String search, int pageNumber, int pageSize) {
        List<Post> posts = postRepository.getPosts(search, pageSize, (pageNumber - 1) * pageSize);
        List<PostFullDto> postFullDtos = postMapper.toListDto(posts);
        postFullDtos.forEach(postFullDto ->
                postFullDto.setComments(commentService.getCommentsByPostId(postFullDto.getId())));
        PagingParametersDto pagingParametersDto = PagingParametersDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .hasNext(pageNumber < Math.ceilDiv(postRepository.getPostsCount(), pageSize))
                .hasPrevious(pageNumber > 1)
                .build();
        return new PostsWithParametersDto(postFullDtos, search, pagingParametersDto);

    }

    @Transactional
    public Post getPostById(Long id) {
        return postRepository.getById(id).orElseThrow();
    }

    @Transactional
    public PostFullDto getPostFullDtoById(Long id) {
        PostFullDto postFullDto = postMapper.toDto(getPostById(id));
        if (postFullDto != null)
            postFullDto.setComments(commentService.getCommentsByPostId(postFullDto.getId()));
        return postFullDto;
    }

    @Transactional
    public PostFullDto savePost(PostDto postDto) {
        PostFullDto postFullDto = getPostFullDtoById(postRepository.save(postMapper.toPost(postDto)));
        if (postFullDto != null)
            postFullDto.setComments(commentService.getCommentsByPostId(postFullDto.getId()));
        return postFullDto;
    }

    @Transactional
    public byte[] getImage(Long postId) {
        return postRepository.getById(postId).orElse(new Post()).getImage();
    }

    @Transactional
    public void likePostBById(Long id, boolean like) {
        int currentLikesCount = 0;
        Post post = getPostById(id);
        if (post != null) {
            currentLikesCount = post.getLikes_count();
        }
        postRepository.likeById(id, (like ? currentLikesCount + 1 :
                (currentLikesCount > 0 ? currentLikesCount - 1 : 0)));
    }

    @Transactional
    public PostDto getPostDtoById(Long id) {
        return postMapper.toPostDto(getPostById(id));
    }

    @Transactional
    public void editPostById(Long id, PostDto postDto) {
        postRepository.updateById(id, postMapper.toPost(postDto));
    }

    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }
}

