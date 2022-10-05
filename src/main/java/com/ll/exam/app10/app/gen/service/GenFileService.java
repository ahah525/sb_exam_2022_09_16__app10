package com.ll.exam.app10.app.gen.service;

import com.ll.exam.app10.app.article.entity.Article;
import com.ll.exam.app10.app.base.AppConfig;
import com.ll.exam.app10.app.base.dto.RsData;
import com.ll.exam.app10.app.gen.entity.GenFile;
import com.ll.exam.app10.app.gen.repository.GenFileRepository;
import com.ll.exam.app10.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenFileService {
    private final GenFileRepository genFileRepository;

    private String getCurrentDirName(String relTypeCode) {
        return relTypeCode + "/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
    }

    @Transactional
    public GenFile save(GenFile genFile) {
        Optional<GenFile> opOldGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(genFile.getRelTypeCode(), genFile.getRelId(), genFile.getTypeCode(), genFile.getType2Code(), genFile.getFileNo());
        // 해당 게시글의 이미지 번호에 이미 파일이 있는지 검사
        if(opOldGenFile.isPresent()) {
            GenFile oldGenFile = opOldGenFile.get();    // 수정전 파일
            deleteFileFromStorage(oldGenFile);

            oldGenFile.merge(genFile);
            genFileRepository.save(oldGenFile);

            return oldGenFile;
        }
        genFileRepository.save(genFile);

        return genFile;
    }

    // 로컬 저장소에서 삭제
    private void deleteFileFromStorage(GenFile genFile) {
        new File(genFile.getFilePath()).delete();
    }

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
            String fileDir = getCurrentDirName(relTypeCode);

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

            genFile = save(genFile);

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

    public void addGenFileByUrl(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, String url) {
        String fileDir = getCurrentDirName(relTypeCode);

        String downFilePath = Util.file.downloadImg(url, AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + UUID.randomUUID());

        File downloadedFile = new File(downFilePath);

        String originFileName = downloadedFile.getName();
        String fileExt = Util.file.getExt(originFileName);
        String fileExtTypeCode = Util.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Util.file.getFileExtType2CodeFromFileExt(fileExt);
        int fileSize = 0;
        try {
            fileSize = (int) Files.size(Paths.get(downFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

        genFile = save(genFile);

        String filePath = AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();

        File file = new File(filePath);

        file.getParentFile().mkdirs();

        downloadedFile.renameTo(file);
    }

    // TODO: 복습
    public Map<String, GenFile> getRelGenFileMap(Article article) {
        // 해당 게시글과 관련된 파일 리스트 조회
        List<GenFile> genFiles = genFileRepository.findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc("article", article.getId());

        return getRelGenFileMap(genFiles);
    }

    public Map<String, GenFile> getRelGenFileMap(List<GenFile> genFiles) {
        // Map 으로 반환
        return genFiles
                .stream()
                .collect(Collectors.toMap(
                        // key =
                        genFile -> genFile.getTypeCode() + "__" + genFile.getType2Code() + "__" + genFile.getFileNo(),
                        genFile -> genFile,
                        // 정렬 순서 유지하기 위해 LinkedHashMap
                        (genFile1, genFile2) -> genFile1,
                        LinkedHashMap::new
                ));
    }

    public void deleteFiles(Article article, Map<String, String> params) {
        // delete___common__inBody__1 에서 common__inBody__1 만 추출하기
        List<String> deleteFilesArgs = params.keySet()
                .stream()
                .filter(key -> key.startsWith("delete___"))
                .map(key -> key.replace("delete___", ""))
                .collect(Collectors.toList());

        deleteFiles(article, deleteFilesArgs);
    }

    public void deleteFiles(Article article, List<String> params) {
        System.out.println(params);

        String relTypeCode = "article";
        Long relId = article.getId();

        params
                .stream()
                .forEach(key -> {
                    String[] keyBits = key.split("__");

                    String typeCode = keyBits[0];
                    String type2Code = keyBits[1];
                    int fileNo = Integer.parseInt(keyBits[2]);

                    Optional<GenFile> optGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(relTypeCode, relId, typeCode, type2Code, fileNo);

                    if(optGenFile.isPresent()) {
                        delete(optGenFile.get());
                    }
                });
    }

    private void delete(GenFile genFile) {
        // 로컬 저장소에서 삭제
        deleteFileFromStorage(genFile);
        // DB에서 삭제
        genFileRepository.delete(genFile);
    }

    public Optional<GenFile> getById(Long id) {
        return genFileRepository.findById(id);
    }

    public List<GenFile> getRelGenFilesByRelIdIn(String relTypeCode, long[] relIds) {
        return genFileRepository.findAllByRelTypeCodeAndRelIdInOrderByTypeCodeAscType2CodeAscFileNoAsc(relTypeCode, relIds);
    }
}