package com.ll.exam.app10.app.article.repository;

import com.ll.exam.app10.app.article.entity.Article;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ll.exam.app10.app.article.entity.QArticle.article;
import static com.ll.exam.app10.app.hashTag.entity.QHashTag.hashTag;
import static com.ll.exam.app10.app.keyword.entity.QKeyword.keyword;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Article> findAllByKwTypeAndKw(String kwType, String kw) {
        return jpaQueryFactory
                .select(article)
                .distinct()
                .from(article)
                .innerJoin(hashTag)
                .on(hashTag.article.eq(article))
                .innerJoin(keyword)
                .on(hashTag.keyword.eq(keyword))
                .where(keyword.content.like(kw))
                .fetch();
    }
}
