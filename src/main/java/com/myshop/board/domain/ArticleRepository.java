package com.myshop.board.domain;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ArticleRepository extends Repository<Articles, Long> {
    void save(Articles articles);

    Optional<Articles> findById(Long id);
}
