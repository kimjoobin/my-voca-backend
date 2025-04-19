package kimjb.japanese_voca.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"level", "category"})
@Builder
@Comment("단어")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id", nullable = false, columnDefinition = "BIGINT(20) UNSIGNED COMMENT '단어 pk'")
    private Long id;

    private String japaneseWord;

    private String hiragana;

    private String meaning;

    private String exampleJa;

    private String exampleKoTranslate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public static Word createWord(Level level, Category category, String kanji, String hiragana, String meaning) {
        return Word.builder()
                .level(level)
                .category(category)
                .japaneseWord(kanji)
                .hiragana(hiragana)
                .meaning(meaning)
                .build();
    }

}
