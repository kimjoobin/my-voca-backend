package kimjb.japanese_voca.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordExampleDto {

    private String exampleKanji;

    private String exampleHiragana;

    private String exampleKoTranslate;
}
