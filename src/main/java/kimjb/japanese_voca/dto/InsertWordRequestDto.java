package kimjb.japanese_voca.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InsertWordRequestDto {
    private Long wordId;
    private String kanji;
    private String hiragana;
    private String meaning;
    private List<MeanDto> meanings = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeanDto {
        private Long id;
        private Long wordId;
        private Integer meanOrder;
        private String exampleKanji;
        private String exampleHiragana;
        private String exampleKo;
    }
}
