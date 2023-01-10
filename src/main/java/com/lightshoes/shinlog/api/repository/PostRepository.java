package com.lightshoes.shinlog.api.repository;

import com.lightshoes.shinlog.api.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
