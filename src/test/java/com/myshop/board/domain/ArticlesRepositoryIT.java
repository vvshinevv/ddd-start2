package com.myshop.board.domain;

import com.myshop.helper.DbHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticlesRepositoryIT {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DbHelper dbHelper;

    @BeforeEach
    void setUp() {
        dbHelper = new DbHelper(jdbcTemplate);
        dbHelper.clear();
    }

    @Test
    void save() {
        Articles aritcle = new Articles("title",
                new ArticleContent("content", "type")
        );
        articleRepository.save(aritcle);

        SqlRowSet rsArticle = jdbcTemplate.queryForRowSet(
                "select * from articles where id = ?",
                aritcle.getId());
        assertThat(rsArticle.next()).isTrue();
        assertThat(rsArticle.getString("title")).isEqualTo("title");

        SqlRowSet rsContent = jdbcTemplate.queryForRowSet(
                "select * from article_content where id = ?",
                aritcle.getId());
        assertThat(rsContent.next()).isTrue();
        assertThat(rsContent.getString("content")).isEqualTo("content");
        assertThat(rsContent.getString("content_type")).isEqualTo("type");
    }

    @Test
    void findByIdNoData() {
        assertThat(articleRepository.findById(0L)).isEmpty();
    }

    @Test
    void findById() {
        jdbcTemplate.update("insert into articles values (100, 'title')");
        jdbcTemplate.update("insert into article_content values (100, 'content', 'type')");

        Optional<Articles> articleOpt = articleRepository.findById(100L);
        assertThat(articleOpt).isPresent();
        Articles articles = articleOpt.get();
        assertThat(articles.getTitle()).isEqualTo("title");
        assertThat(articles.getContent().getContent()).isEqualTo("content");
        assertThat(articles.getContent().getContentType()).isEqualTo("type");
    }
}
