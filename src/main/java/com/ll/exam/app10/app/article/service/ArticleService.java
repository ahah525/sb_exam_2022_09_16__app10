package com.ll.exam.app10.app.article.service;

import com.ll.exam.app10.app.article.entity.Article;
import com.ll.exam.app10.app.article.repository.ArticleRepository;
import com.ll.exam.app10.app.gen.entity.GenFile;
import com.ll.exam.app10.app.gen.service.GenFileService;
import com.ll.exam.app10.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final GenFileService genFileService;

    public Article write(Long authorId, String subject, String content) {
        return write(new Member(authorId), subject, content);
    }

    public Article write(Member author, String subject, String content)  {
        Article article = Article.builder()
                .subject(subject)
                .content(content)
                .author(author)
                .build();

        return articleRepository.save(article);
    }
    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public void addGenFileByUrl(Article article, String typeCode, String type2Code, int fileNo, String url) {
        genFileService.addGenFileByUrl("article", article.getId(), typeCode, type2Code, fileNo, url);
    }

    public Article getForPrintArticleById(Long id) {
        Article article = getArticleById(id);

        Map<String, GenFile> genFileMap = genFileService.getRelGenFileMap(article);

//        article.getExtra().put("age", 22);
        article.getExtra().put("age__name", 22);    // 나이 추가
        article.getExtra().put("genFileMap", genFileMap);  // 게시글과 관련된 파일들

        return article;
    }

    public void modify(Article article, String subject, String content) {
        article.setSubject(subject);
        article.setContent(content);
        articleRepository.save(article);
    }
}
