package com.lightshoes.shinlog.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lightshoes.shinlog.api.domain.PostEditor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PostEdit {

    @NotBlank(message = "타이틀을 입력해주세요.")
    private String title;

    @NotBlank(message = "컨텐츠를 입력해주세요.")
    private String content;

    @Builder
    @JsonCreator
    public PostEdit(@JsonProperty("title") String title, @JsonProperty("content")String content) {
        this.title = title;
        this.content = content;
    }
}
