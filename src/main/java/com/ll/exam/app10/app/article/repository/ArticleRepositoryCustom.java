package com.ll.exam.app10.app.article.repository;

import com.ll.exam.app10.app.article.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findAllByKwTypeAndKw(String kwType, String kw);
}
