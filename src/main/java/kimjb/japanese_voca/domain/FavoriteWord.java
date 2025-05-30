package kimjb.japanese_voca.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Comment("즐겨찾기 단어")
public class FavoriteWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    public static FavoriteWord createFavoriteWord(Word word, User user) {
        return FavoriteWord.builder()
                .word(word)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
