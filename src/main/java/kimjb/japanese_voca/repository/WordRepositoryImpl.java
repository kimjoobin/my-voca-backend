package kimjb.japanese_voca.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kimjb.japanese_voca.domain.Word;
import kimjb.japanese_voca.dto.JLPTWordResponseDto;
import kimjb.japanese_voca.dto.WordExampleDto;
import kimjb.japanese_voca.repository.custom.CustomWordRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static kimjb.japanese_voca.domain.QFavoriteWord.favoriteWord;
import static kimjb.japanese_voca.domain.QLevel.level;
import static kimjb.japanese_voca.domain.QWord.word;
import static kimjb.japanese_voca.domain.QWordMeaning.wordMeaning;

@Repository
@RequiredArgsConstructor
public class WordRepositoryImpl implements CustomWordRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<JLPTWordResponseDto> getJPLTWord(String chapter,
                                                 String wordLevel,
                                                 String searchVal) {

        int chapterNum = Integer.parseInt(chapter.substring(chapter.lastIndexOf("r") + 1)) - 1;

        List<JLPTWordResponseDto> query =  queryFactory
                .select(
                    Projections.fields(
                        JLPTWordResponseDto.class
                        ,word.id.as("wordId")
                        ,Expressions.asString(chapter).as("chapter")
                        ,word.japaneseWord.as("word")
                        ,word.hiragana.as("hiragana")
                        ,word.meaning.as("meaning")
                        , Expressions.cases()
                            .when(favoriteWord.word.id.isNull())
                            .then(false)
                            .otherwise(true)
                                    .as("isFavorite")
                        , level.name.as("jlptLevel")
                    )
                )
                .distinct()
                .from(word)
                .innerJoin(level).on(word.level.eq(level)
                        .and(level.name.stringValue().eq(wordLevel))
                )
                .leftJoin(favoriteWord).on(favoriteWord.word.eq(word))
                .leftJoin(wordMeaning).on(wordMeaning.word.eq(word))
                .orderBy(word.hiragana.asc())
                .offset(chapterNum * 20L)
                .limit(20)
                .fetch();

        fetchWordExample(query);

        return query;
    }

    private void fetchWordExample(List<JLPTWordResponseDto> query) {
        List<Long> wordIds = query.stream()
                .map(JLPTWordResponseDto::getWordId)
                .toList();

        // 3. 예제 단어 데이터 조회 및 매핑
        if (!wordIds.isEmpty()) {
            Map<Long, List<WordExampleDto>> examplesMap = fetchWordExamplesWithProjection(wordIds);

            // 4. 각 단어에 예제 추가
            query.forEach(wordDto ->
                    wordDto.setWordExamples(examplesMap.getOrDefault(wordDto.getWordId(), new ArrayList<>()))
            );
        }
    }

    // 검색 조건 처리를 위한 헬퍼 메서드
    private BooleanExpression containsSearchVal(String searchVal) {
        if (StringUtils.isEmpty(searchVal)) {
            return null;
        }
        return word.japaneseWord.contains(searchVal)
                .or(word.hiragana.contains(searchVal))
                .or(word.meaning.contains(searchVal));
    }

    // 중간 매핑을 위한 내부 클래스
    @Getter
    @Setter
    @NoArgsConstructor
    public static class WordExampleMapping {
        private Long wordId;
        private String exampleKanji;
        private String exampleHiragana;
        private String exampleKoTranslate;
    }

    private Map<Long, List<WordExampleDto>> fetchWordExamplesWithProjection(List<Long> wordIds) {
        // wordExample 엔티티에서 직접 WordExampleDto 프로젝션으로 조회
        List<WordExampleMapping> exampleResults = queryFactory
                .select(
                        Projections.fields(WordExampleMapping.class,
                                wordMeaning.word.id.as("wordId"),
                                wordMeaning.exampleKanji,
                                wordMeaning.exampleHiragana,
                                wordMeaning.exampleKoTranslate
                        )
                )
                .from(wordMeaning)
                .where(wordMeaning.word.id.in(wordIds))
                .fetch();

        // 결과를 단어 ID별로 그룹화
        Map<Long, List<WordExampleDto>> examplesMap = new HashMap<>();

        for (WordExampleMapping mapping : exampleResults) {
            Long wordId = mapping.getWordId();

            WordExampleDto exampleDto = new WordExampleDto(
                    mapping.getExampleKanji(),
                    mapping.getExampleHiragana(),
                    mapping.getExampleKoTranslate()
            );

            if (!examplesMap.containsKey(wordId)) {
                examplesMap.put(wordId, new ArrayList<>());
            }

            examplesMap.get(wordId).add(exampleDto);
        }

        return examplesMap;
    }


    @Override
    public Map<String, Object> getChapterByLevel(String wordLevel) {
        Map<String, Object> result = new HashMap<>();

        List<Word> words = queryFactory
                .selectFrom(word)
                .innerJoin(level).on(level.eq(word.level))
                .where(level.name.stringValue().eq(wordLevel))
                .orderBy(word.hiragana.asc())
                .fetch();

        int totalWords = words.size();
        int totalChapters = (int)Math.ceil(totalWords / 20.0);

        List<String> chapter = IntStream.rangeClosed(1, totalChapters)
                .mapToObj(i -> "Chapter" + i)
                .collect(Collectors.toList());

        result.put("chapter", chapter);
        result.put("totalWords", totalWords);

        return result;
    }

    @Override
    public List<JLPTWordResponseDto> getTodayWord() {
        // 오늘 날짜를 기준으로 시드 값 생성
        LocalDate today = LocalDate.now();
        int seed = today.getYear() * 10000 + today.getMonthValue() * 100 + today.getDayOfMonth();

        List<JLPTWordResponseDto> query = queryFactory
                .select(
                    Projections.fields(
                        JLPTWordResponseDto.class
                        ,word.id.as("wordId")
                        ,word.japaneseWord.as("word")
                        ,word.hiragana.as("hiragana")
                        ,word.meaning.as("meaning")
                        , Expressions.cases()
                            .when(favoriteWord.word.id.isNull())
                            .then(false)
                            .otherwise(true)
                            .as("isFavorite")
                        , level.name.as("jlptLevel")
                    )
                )
                .from(word)
                .innerJoin(level).on(word.level.eq(level))
                .leftJoin(favoriteWord).on(favoriteWord.word.eq(word))
                .orderBy(Expressions.numberTemplate(Double.class, "RAND({0})", seed).asc())
                .limit(5)
                .fetch()
                ;

        fetchWordExample(query);

        return query;
    }

    @Override
    public List<JLPTWordResponseDto> getSearchWord(String searchText) {
        return queryFactory
                .select(
                        Projections.fields(
                                JLPTWordResponseDto.class
                                ,word.id.as("wordId")
                                ,word.japaneseWord.as("word")
                                ,word.hiragana.as("hiragana")
                                ,word.meaning.as("meaning")
                                , level.name.as("jlptLevel")
                        )
                )
                .from(word)
                .where(
                        containsSearchTerm(searchText)
                )
                .fetch();
    }

    // 검색어 조건 메서드
    private BooleanExpression containsSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return null; // 검색어가 없으면 조건 없음
        }

        return word.hiragana.containsIgnoreCase(searchTerm)
                .or(word.meaning.contains(searchTerm))
                .or(word.hiragana.containsIgnoreCase(searchTerm));
    }
}
