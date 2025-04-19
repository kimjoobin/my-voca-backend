package kimjb.japanese_voca.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Comment("단어 예시문")
public class WordMeaning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    private Integer meanOrder;

    private String exampleHiragana;

    private String exampleKanji;

    private String exampleKoTranslate;

    // 필요한 필드만 받는 정적 팩토리 메서드
    public static WordMeaning createWordMeaning(Word word,
                                                Integer meanOrder,
                                                String exampleKanji,
                                                String exampleHiragana,
                                                String exampleKoTranslate) {

        WordMeaning wordMeaning = new WordMeaning();
        wordMeaning.word = word;
        wordMeaning.meanOrder = meanOrder;
        wordMeaning.exampleKanji = exampleKanji;
        wordMeaning.exampleHiragana = exampleHiragana;
        wordMeaning.exampleKoTranslate = exampleKoTranslate;

        return wordMeaning;
    }

    // 필요시 업데이트 메서드 추가
    public WordMeaning updateExample(String exampleKanji, String exampleHiragana, String exampleKoTranslate) {
        this.exampleKanji = exampleKanji;
        this.exampleHiragana = exampleHiragana;
        this.exampleKoTranslate = exampleKoTranslate;
        return this;
    }
}
