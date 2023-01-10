package com.lightshoes.shinlog.api.service;

import com.lightshoes.shinlog.api.domain.Post;
import com.lightshoes.shinlog.api.exception.PostNotFound;
import com.lightshoes.shinlog.api.repository.PostRepository;
import com.lightshoes.shinlog.api.request.PostCreate;
import com.lightshoes.shinlog.api.request.PostEdit;
import com.lightshoes.shinlog.api.request.PostSearch;
import com.lightshoes.shinlog.api.response.PostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void writePost() throws Exception {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        //when
        postService.write(postCreate);

        //then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다", post.getTitle());
        assertEquals("내용입니다", post.getContent());
    }

    @Test
    @DisplayName("글 단건 조회")
    void getPost() {
        // given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        // when
        PostResponse response = postService.get(requestPost.getId());

        // then
        assertNotNull(response);
        assertEquals(1L, postRepository.count());
        assertEquals("foo", response.getTitle());
        assertEquals("bar", response.getContent());
    }

    @Test
    @DisplayName("글 단건 조회 실패")
    void getPostFail() {
        // given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        Long FOUND_FAIL_ID = 1000L;

        // expected
        PostNotFound e = assertThrows(PostNotFound.class, () -> {
            PostResponse response = postService.get(FOUND_FAIL_ID);
        });
    }

    @Test
    @DisplayName("글 1 페이지 조회")
    void getPosts() {
        // given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                        .title("광 제목 " + i)
                        .content("도곡렉슬 " + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertEquals(10L, posts.size());
        assertEquals("광 제목 19", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void updatePostTitle() {
        // given
        Post post = Post.builder()
                .title("광 제목")
                .content("도곡렉슬")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("신신광")
                .build();


        // expected
        postService.edit(post.getId(), postEdit);

        // then
        Post changePost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("신신광", changePost.getTitle());
        assertEquals("도곡렉슬", changePost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void updatePostContent() {
        // given
        Post post = Post.builder()
                .title("광 제목")
                .content("도곡렉슬")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .content("초가집")
                .build();


        // when
        postService.edit(post.getId(), postEdit);

        // then
        Post changePost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("광 제목", changePost.getTitle());
        assertEquals("초가집", changePost.getContent());
    }

    @Test
    @DisplayName("게시글 수정 - 존재하지 않는 글")
    void updatePostFailByNotFoundPost() {
        // given
        Post post = Post.builder()
                .title("광 제목")
                .content("도곡렉슬")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .content("초가집")
                .build();

        Long FOUND_FAIL_ID = 1000L;

        // expected
        Assertions.assertThrows(PostNotFound.class,
                () -> postService.edit(FOUND_FAIL_ID, postEdit));
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() {
        // given
        Post post = Post.builder()
                .title("광 제목")
                .content("도곡렉슬")
                .build();
        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertEquals(0L, postRepository.count());
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 글")
    void deletePostFailByNotFoundPost() {
        // given
        Post post = Post.builder()
                .title("광 제목")
                .content("도곡렉슬")
                .build();
        postRepository.save(post);

        Long FOUND_FAIL_ID = 1000L;

        // expected
        assertThrows(PostNotFound.class, () -> postService.delete(FOUND_FAIL_ID));
    }
}