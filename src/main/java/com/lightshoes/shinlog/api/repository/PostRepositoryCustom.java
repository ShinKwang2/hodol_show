package com.lightshoes.shinlog.api.repository;

import com.lightshoes.shinlog.api.domain.Post;
import com.lightshoes.shinlog.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
