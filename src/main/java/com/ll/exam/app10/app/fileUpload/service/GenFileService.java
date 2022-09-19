package com.ll.exam.app10.app.fileUpload.service;

import com.ll.exam.app10.app.article.entity.Article;
import com.ll.exam.app10.app.base.AppConfig;
import com.ll.exam.app10.app.base.dto.RsData;
import com.ll.exam.app10.app.fileUpload.entity.GenFile;
import com.ll.exam.app10.app.fileUpload.repository.GenFileRepository;
import com.ll.exam.app10.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenFileService {
    private final GenFileRepository genFileRepository;

    public RsData<Map<String, GenFile>> saveFiles(Article article, Map<String, MultipartFile> fileMap) {
        String relTypeCode = "article";
        long relId = article.getId();

        Map<String, GenFile> genFileIds = new HashMap<>();

        for (String inputName : fileMap.keySet()) {
            MultipartFile multipartFile = fileMap.get(inputName);

            if (multipartFile.isEmpty()) {
                continue;
            }

//            String typeCode = "common";
//            String type2Code = "inBody";
//            String fileExt = "jpg";
//            String fileExtTypeCode = "img";
//            String fileExtType2Code = "jpg";
//            int fileNo = 1;
//            int fileSize = 1000;
//            String fileDir = "article/2022_09_19";
//            String originFileName = "??";
            // inputName = common__bodyImg__1
            String[] inputNameBits = inputName.split("__");

            String typeCode = inputNameBits[0];
            String type2Code = inputNameBits[1];
            String originFileName = multipartFile.getOriginalFilename();
            String fileExt = Util.file.getExt(originFileName);
            String fileExtTypeCode = Util.file.getFileExtTypeCodeFromFileExt(fileExt);
            String fileExtType2Code = Util.file.getFileExtType2CodeFromFileExt(fileExt);
            int fileNo = Integer.parseInt(inputNameBits[2]);
            int fileSize = (int) multipartFile.getSize();
            String fileDir = relTypeCode + "/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");

            GenFile genFile = GenFile
                    .builder()
                    .relTypeCode(relTypeCode)
                    .relId(relId)
                    .typeCode(typeCode)
                    .type2Code(type2Code)
                    .fileExtTypeCode(fileExtTypeCode)
                    .fileExtType2Code(fileExtType2Code)
                    .fileNo(fileNo)
                    .fileSize(fileSize)
                    .fileDir(fileDir)
                    .fileExt(fileExt)
                    .originFileName(originFileName)
                    .build();

            genFileRepository.save(genFile);

            // 로컬 저장 파일 경로
            String filePath = AppConfig.GET_FILE_DIR_PATH + "/" + fileDir +  "/" + genFile.getFileName();

            File file = new File(filePath);

            // 실제 파일 저장
            file.getParentFile().mkdirs(); // 폴더가 혹시나 없다면 만들어준다.

            try {
                multipartFile.transferTo(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            genFileIds.put(inputName, genFile);
        }
        // 결과 응답
        return new RsData<>("S-1", "파일을 업로드했습니다.", genFileIds);
    }
}