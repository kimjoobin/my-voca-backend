package kimjb.japanese_voca.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InsertWordRequestDto {

    private String japanese;
    private String hiragana;
    private Long levelId;
    private Long categoryId;
    private List<MeanDto> meanings = new ArrayList<>();

    @Getter
    @Setter
    public static class MeanDto {
        private String meanings;
        private String exampleJa;
        private String exampleKo;
    }
}
