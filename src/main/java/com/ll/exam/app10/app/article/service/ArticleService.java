package com.ll.exam.app10.app.article.service;

import com.ll.exam.app10.app.article.entity.Article;
import com.ll.exam.app10.app.article.repository.ArticleRepository;
import com.ll.exam.app10.app.gen.entity.GenFile;
import com.ll.exam.app10.app.gen.service.GenFileService;
import com.ll.exam.app10.app.hashTag.entity.HashTag;
import com.ll.exam.app10.app.hashTag.service.HashTagService;
import com.ll.exam.app10.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final GenFileService genFileService;
    private final HashTagService hashTagService;

    public Article write(Long authorId, String subject, String content) {
        return write(new Member(authorId), subject, content);
    }

    public Article write(Long authorId, String subject, String content, String hashTagContents) {
        return write(new Member(authorId), subject, content, hashTagContents);
    }

    public Article write(Member author, String subject, String content){
        return write(author, subject, content, "");
    }

    public Article write(Member author, String subject, String content, String hashTagContents)  {
        Article article = Article.builder()
                .subject(subject)
                .content(content)
                .author(author)
                .build();

        articleRepository.save(article);
        hashTagService.applyHashTags(article, hashTagContents);

        return article;
    }
    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public void addGenFileByUrl(Article article, String typeCode, String type2Code, int fileNo, String url) {
        genFileService.addGenFileByUrl("article", article.getId(), typeCode, type2Code, fileNo, url);
    }

    public Article getForPrintArticleById(Long id) {
        Article article = getArticleById(id);

        loadForPrintData(article);

        return article;
    }

    public void modify(Article article, String subject, String content, String hashTagContents) {
        article.setSubject(subject);
        article.setContent(content);
        articleRepository.save(article);

        hashTagService.applyHashTags(article, hashTagContents);
    }

    public List<Article> getArticles() {
        return articleRepository.getQslArticlesOrderByIdDesc();
    }

    public List<Article> search(String kwType, String kw) {
        return articleRepository.searchQsl(kwType, kw);
    }

    public void loadForPrintData(Article article) {
        Map<String, GenFile> genFileMap = genFileService.getRelGenFileMap(article);
        List<HashTag> hashTags = hashTagService.getHashTags(article);

        article.getExtra().put("hashTags", hashTags);
        article.getExtra().put("genFileMap", genFileMap);  // 게시글과 관련된 파일들
    }

    public void loadForPrintData(List<Article> articles) {
        // article id 리스트
        long[] ids = articles
                .stream()
                .mapToLong(Article::getId)
                .toArray();
        // 해당 article id와 관련된 모든 hashTag 를 article id를 key로 하여 map으로 변환
        List<HashTag> hashTagsByArticleIds = hashTagService.getHashTagsByArticleIdIn(ids);

        Map<Long, List<HashTag>> hashTagsByArticleIdsMap = hashTagsByArticleIds.stream()
                .collect(groupingBy(
                        hashTag -> hashTag.getArticle().getId(), toList()
                ));
        // 각 게시글과 관련된 해시태그 저장
        articles.stream().forEach(article -> {
            List<HashTag> hashTags = hashTagsByArticleIdsMap.get(article.getId());

            if(hashTags == null || hashTags.size() == 0) return;

            article.getExtra().put("hashTags", hashTags);
        });

        // 해당 article id와 관련된 모든 genFile 을 article id를 key로 하여 map으로 변환
        List<GenFile> genFilesByRelIdIn = genFileService.getRelGenFilesByRelIdIn("article", ids);

        Map<Long, List<GenFile>> genFilesMap = genFilesByRelIdIn.stream()
                .collect(groupingBy(
                        genFile -> genFile.getRelId(), toList()
                ));
        // 각 게시글과 관련된 파일 저장
        articles.stream().forEach(article -> {
            List<GenFile> genFiles = genFilesMap.get(article.getId());

            if(genFiles == null || genFiles.size() == 0) return;

            article.getExtra().put("genFileMap", genFileService.getRelGenFileMap(genFiles));
        });

        log.debug("articles : " + articles);
    }
}
