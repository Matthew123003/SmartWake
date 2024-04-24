package smartwake.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import smartwake.domain.UserLogin;

/**
 * Spring Data JPA repository for the UserLogin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {}
