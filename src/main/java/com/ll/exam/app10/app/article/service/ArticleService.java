package com.ll.exam.app10.app.article.service;

import com.ll.exam.app10.app.article.entity.Article;
import com.ll.exam.app10.app.article.repository.ArticleRepository;
import com.ll.exam.app10.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Article write(Long authorId, String subject, String content) {
        Article article = Article.builder()
                .subject(subject)
                .content(content)
                .author(new Member(authorId))
                .build();

        return articleRepository.save(article);
    }
    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }
}
