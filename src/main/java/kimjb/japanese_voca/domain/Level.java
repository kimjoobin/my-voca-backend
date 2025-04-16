package kimjb.japanese_voca.domain;

import jakarta.persistence.*;
import kimjb.japanese_voca.enums.LevelEnum;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LevelEnum name;

    private String description;
}
