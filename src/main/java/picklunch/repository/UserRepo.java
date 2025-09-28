package picklunch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import picklunch.model.entity.User;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findAllByUsernameIn(Set<String> username);

}
