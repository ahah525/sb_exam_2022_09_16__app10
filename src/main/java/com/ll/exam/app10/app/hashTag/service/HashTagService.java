package com.ll.exam.app10.app.hashTag.service;

import com.ll.exam.app10.app.article.entity.Article;
import com.ll.exam.app10.app.hashTag.entity.HashTag;
import com.ll.exam.app10.app.hashTag.repository.HashTagRepository;
import com.ll.exam.app10.app.keyword.entity.Keyword;
import com.ll.exam.app10.app.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final KeywordService keywordService;
    private final HashTagRepository hashTagRepository;
    public void applyHashTags(Article article, String hashTagsStr) {
        // 1. 기존 해시태그 가져오기
        List<HashTag> oldHashTags = getHashTags(article);

        // 2. 새 해시태그 리스트로 만들기
        List<String> keywordContents = Arrays.stream(hashTagsStr.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        // 3. 삭제할 키워드 계산
        List<HashTag> needToDelete = new ArrayList<>();

        for(HashTag oldHashTag : oldHashTags) {
            // 기존에 등록된 해시태그가 새롭게 등록된 해시태그에 포함되었는지 여부
            boolean contains = keywordContents.stream().anyMatch(s -> s.equals(oldHashTag.getKeyword().getContent()));

            if(!contains) {
                needToDelete.add(oldHashTag);
            }
        }

        // 4. 해시태그 삭제
        needToDelete.forEach(hashTag -> {
            System.out.println("hashTag = " + hashTag.getKeyword().getContent());
            hashTagRepository.delete(hashTag);
        });

        // 5. 추가할 키워드 계산
        keywordContents.forEach(keywordContent -> {
            System.out.println("keywordContent = " + keywordContent);
            saveHashTag(article, keywordContent);
        });
    }

    private HashTag saveHashTag(Article article, String keywordContent) {
        Keyword keyword = keywordService.save(keywordContent);

        Optional<HashTag> opHashTag = hashTagRepository.findByArticleIdAndKeywordId(article.getId(), keyword.getId());
        // 해당 게시글에 이미 등록된 해시태그인지 검사
        if(opHashTag.isPresent()) {
            return opHashTag.get();
        }

        HashTag hashTag = HashTag
                .builder()
                .article(article)
                .keyword(keyword)
                .build();

        hashTagRepository.save(hashTag);

        return hashTag;
    }

    public List<HashTag> getHashTags(Article article) {
        return hashTagRepository.findAllByArticleId(article.getId());
    }
}
