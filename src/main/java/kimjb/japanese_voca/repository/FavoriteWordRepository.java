package kimjb.japanese_voca.repository;

import kimjb.japanese_voca.domain.FavoriteWord;
import kimjb.japanese_voca.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteWordRepository extends JpaRepository<FavoriteWord, Long> {

    void deleteByWord(Word word);

    FavoriteWord findByWord(Word word);
}
