package kimjb.japanese_voca.repository;

import kimjb.japanese_voca.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
