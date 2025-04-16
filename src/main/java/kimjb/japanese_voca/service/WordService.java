package kimjb.japanese_voca.service;

import com.opencsv.CSVReader;
import kimjb.japanese_voca.domain.*;
import kimjb.japanese_voca.dto.InsertWordRequestDto;
import kimjb.japanese_voca.dto.JLPTWordResponseDto;
import kimjb.japanese_voca.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class WordService {

    private final WordRepository wordRepository;
    private final WordMeaningRepository wordMeaningRepository;
    private final FavoriteWordRepository favoriteWordRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final CategoryRepository categoryRepository;

    private static final Map<String, Long> levelMap = Map.of(
            "N5", 1L, "N4", 2L, "N3", 3L, "N2", 4L, "N1", 5L
    );

    private static final Map<String, Long> categoryMap = Map.of(
            "동사", 1L, "명사", 2L, "형용사", 3L, "부사", 4L,
            "대명사", 5L, "조수사", 6L, "기타", 7L, "형용동사", 8L, "접속사", 9L
    );

    public void importWordsFromCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                String levelName = nextLine[0];  // N5, N4, ...
                String categoryName = nextLine[3]; // 동사, 명사, ...

                // Level 조회
                Long levelId = levelMap.getOrDefault(levelName, 0L);
                Level level = levelRepository.findById(levelId)
                        .orElseThrow(() -> new RuntimeException("Level " + levelName + " not found"));

                // Category 조회
                Long categoryId = categoryMap.getOrDefault(categoryName, 7L); // 기본값: 기타
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category " + categoryName + " not found"));

                // 한자, 히라가나 분리
                String[] wordParts = nextLine[1].split("\\[");
                String hiragana = wordParts[0];
                String kanji = wordParts.length > 1 ? wordParts[1].replace("]", "") : null;

                // 뜻이 여러 개인 경우 쉼표로 구분되어 있으므로, 따옴표로 묶은 부분을 그대로 읽음
                String meanings = nextLine[2]; // "조절, 조정" 또는 "조정" 하나일 수 있음
                String[] meaningArray = meanings.split("，");  // 한글 쉼표로 구분

                // 뜻이 하나일 경우 그냥 하나만 저장, 여러 개일 경우 여러 개 저장
                if (meaningArray.length == 1) {
                    Word word = Word.createWord(level, category, kanji, hiragana, meaningArray[0].trim());
                    wordRepository.save(word);
                } else {
                    for (String meaning : meaningArray) {
                        Word word = Word.createWord(level, category, kanji, hiragana, meaning.trim());
                        wordRepository.save(word);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getChapterByLevel(String level) {
        return wordRepository.getChapterByLevel(level);
    }

    @Transactional(readOnly = true)
    public List<JLPTWordResponseDto> getJLPTWord(String chapter, String level, String searchVal) {
        return wordRepository.getJPLTWord(chapter, level, searchVal);
    }

    @Transactional(readOnly = true)
    public void getSearchWord() {
    }

    public void createFavoriteWord(Long wordId) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Word word = wordRepository.findById(wordId)
                .orElse(null);

        FavoriteWord favoriteWord = favoriteWordRepository.findByWord(word);

        if (favoriteWord != null) {
            // 즐겨찾기 해제
            favoriteWordRepository.deleteByWord(word);
        } else {
            // 즐겨찾기 등록
            favoriteWordRepository.save(FavoriteWord.createFavoriteWord(word, user));
        }
    }

    @Transactional(readOnly = true)
    public List<JLPTWordResponseDto> getTodayWord() {
        return wordRepository.getTodayWord();
    }

    public void getWordExamples() {
    }

    public void uploadExampleCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] nextLine;
            // 헤더 행 건너뛰기 (있는 경우)

            while ((nextLine = reader.readNext()) != null) {
                try {
                    // CSV 파일 형식에 맞게 데이터 추출
                    Long wordId = Long.parseLong(nextLine[0]);
                    Integer meanOrder = Integer.parseInt(nextLine[1]);
                    String exampleKanji = nextLine[2];
                    String exampleHiragana = nextLine[3];
                    String exampleKoTranslate = nextLine[4];

                    // Word 엔티티 찾기
                    Word word = wordRepository.findById(wordId)
                            .orElseThrow(() -> new RuntimeException("Word ID " + wordId + " not found"));

                    // 이미 해당 word와 mean_order로 존재하는지 확인
                    Optional<WordMeaning> existingMeaning = wordMeaningRepository
                            .findByWordAndMeanOrder(word, meanOrder);

                    WordMeaning wordMeaning;
                    if (existingMeaning.isPresent()) {
                        // 기존 데이터 업데이트
                        wordMeaning = existingMeaning.get()
                                .updateExample(exampleKanji, exampleHiragana, exampleKoTranslate);
                    } else {
                        // 새 데이터 생성
                        wordMeaning = WordMeaning.createWordMeaning(
                                word, meanOrder, exampleKanji, exampleHiragana, exampleKoTranslate);
                    }

                    wordMeaningRepository.save(wordMeaning);
                } catch (Exception e) {
                    System.err.println("행 처리 중 오류: " + Arrays.toString(nextLine));
                    e.printStackTrace();
                    // 한 행에서 오류가 발생해도 계속 진행
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("CSV 파일 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
