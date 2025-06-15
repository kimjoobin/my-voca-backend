package kimjb.japanese_voca.dto;

import kimjb.japanese_voca.domain.Word;
import kimjb.japanese_voca.enums.LevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordResponse {

    private Long wordId;
    private LevelEnum level;
    private String kanji;
    private String hiragana;
    private String mean;

    public static WordResponse from(Word word) {
        return WordResponse.builder()
                .wordId(word.getId())
                .level(word.getLevel().getName())
                .kanji(word.getJapaneseWord())
                .hiragana(word.getHiragana())
                .mean(word.getMeaning())
                .build();
    }
}
