package kimjb.japanese_voca.repository;

import kimjb.japanese_voca.domain.Word;
import kimjb.japanese_voca.domain.WordMeaning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordMeaningRepository extends JpaRepository<WordMeaning, Long> {
    Optional<WordMeaning> findByWordAndMeanOrder(Word word, Integer meanOrder);
}
