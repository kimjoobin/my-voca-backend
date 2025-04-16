package kimjb.japanese_voca.dto;

import com.querydsl.core.annotations.QueryProjection;
import kimjb.japanese_voca.enums.LevelEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JLPTWordResponseDto {

    private Long wordId;

    private String chapter;

    private String word;

    private String hiragana;

    private String meaning;

    private boolean isFavorite;

    private LevelEnum jlptLevel;

    private List<WordExampleDto> wordExamples = new ArrayList<>();

}
