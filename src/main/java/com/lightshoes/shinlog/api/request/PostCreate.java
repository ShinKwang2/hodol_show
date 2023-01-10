package com.lightshoes.shinlog.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lightshoes.shinlog.api.exception.InvalidRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter @Getter
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class PostCreate {

    @NotBlank(message = "타이틀을 입력해주세요.")
    private String title;

    @NotBlank(message = "컨텐츠를 입력해주세요.")
    private String content;

    @Builder
    @JsonCreator
    public PostCreate(@JsonProperty("title") String title, @JsonProperty("content")String content) {
        this.title = title;
        this.content = content;
    }

    public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequest("title", "제목에 바보를 포함할 수 없습니다.");
        }
    }
}
