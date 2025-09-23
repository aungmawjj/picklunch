package picklunch.repository;

import picklunch.model.entity.LunchPicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LunchPickerRepo extends JpaRepository<LunchPicker, Long> {

    long countByStateNot(LunchPicker.State state);

}
