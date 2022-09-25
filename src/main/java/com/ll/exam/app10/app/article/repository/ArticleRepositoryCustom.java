package com.ll.exam.app10.app.article.repository;

import com.ll.exam.app10.app.article.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> getQslArticlesOrderByIdDesc();

    List<Article> searchQsl(String kwType, String kw);
}
