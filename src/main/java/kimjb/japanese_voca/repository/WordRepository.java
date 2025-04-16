package kimjb.japanese_voca.repository;

import kimjb.japanese_voca.domain.Word;
import kimjb.japanese_voca.repository.custom.CustomWordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long>, CustomWordRepository {
}
