package kimjb.japanese_voca.repository;

import kimjb.japanese_voca.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
