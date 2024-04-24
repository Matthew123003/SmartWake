package smartwake.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import smartwake.domain.Alarm;

/**
 * Spring Data JPA repository for the Alarm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlarmRepository extends JpaRepository<Alarm, String> {}
