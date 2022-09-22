package com.ll.exam.app10.app.keyword.service;

import com.ll.exam.app10.app.keyword.entity.Keyword;
import com.ll.exam.app10.app.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    public Keyword save(String keywordContent) {
        // 같은 키워드가 있는지 검사
        Optional<Keyword> optKeyword = keywordRepository.findByContent(keywordContent);

        if(optKeyword.isPresent()) {
            return optKeyword.get();
        }
        // 없으면 새롭게 저장
        Keyword keyword = Keyword
                .builder()
                .content(keywordContent)
                .build();
        keywordRepository.save(keyword);

        return keyword;
    }
}
