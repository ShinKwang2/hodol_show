package com.lightshoes.shinlog.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightshoes.shinlog.api.domain.Post;
import com.lightshoes.shinlog.api.repository.PostRepository;
import com.lightshoes.shinlog.api.request.PostCreate;
import com.lightshoes.shinlog.api.request.PostEdit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /posts 요청시 Hello World를 출력한다.")
    void postPosts() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());

    }

    @Test
    @DisplayName("POST /posts 요청시 title값은 필수다.")
    void validatePost() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .content("내용입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("POST /posts 요청시 DB에 값이 저장된다.")
    void savePost() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\": \"제목입니다\", \"content\": \"내용입니다\"}")
                )
                .andExpect(status().isOk())
                .andDo(print());

        // then
        assertEquals(1L, postRepository.count());
        org.assertj.core.api.Assertions.assertThat(postRepository.count()).isEqualTo(1L);

        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다", post.getTitle());
        assertEquals("내용입니다", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void getOnePost() throws Exception {
        // given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 페이지 1 조회")
    void getPosts() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("광 제목 " + i)
                        .content("도곡렉슬 " + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("광 제목 30"))
                .andExpect(jsonPath("$[0].content").value("도곡렉슬 30"))
                .andDo(print());
    }

    @Test
    @DisplayName("페이지를 0으로 요청하면 첫 페이지를 가져온다.")
    void getQueryParameterPageEqual0() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("광 제목 " + i)
                        .content("도곡렉슬 " + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/posts?page=0&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("광 제목 30"))
                .andExpect(jsonPath("$[0].content").value("도곡렉슬 30"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 제목 수정")
    void updatePostTitle() throws Exception {
        // given
        Post post = Post.builder()
                .title("광 제목")
                .content("도곡렉슬")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("신신광")
                .content("도곡렉슬")
                .build();

        // expected
        mockMvc.perform(patch("/posts/{postId}", post.getId()) // PATCH /posts/{postId}
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() throws Exception {
        // given
        Post post = Post.builder()
                .title("광 제목")
                .content("도곡렉슬")
                .build();

        postRepository.save(post);

        // expected
        mockMvc.perform(delete("/posts/{postId}", post.getId()) // PATCH /posts/{postId}
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void getPostFailByNotFoundPost() throws Exception {
        // given
        Long FOUND_FAIL_ID = 1000L;

        // expected
        mockMvc.perform(get("/posts/{postId}", FOUND_FAIL_ID)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void patchPostFailByNotFoundPost() throws Exception {
        // given
        Long FOUND_FAIL_ID = 1000L;

        PostEdit postEdit = PostEdit.builder()
                .title("신신광")
                .content("도곡렉슬")
                .build();

        // expected
        mockMvc.perform(patch("/posts/{postId}", FOUND_FAIL_ID)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제")
    void deletePostFailByNotFoundPost() throws Exception {
        // given
        Long FOUND_FAIL_ID = 1000L;

        // expected
        mockMvc.perform(delete("/posts/{postId}", FOUND_FAIL_ID)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성시 제목에 '바보'는 포함될 수 없다.")
    void postNotInclude바보() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("나는 바보입니다.")
                .content("도곡렉슬")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}