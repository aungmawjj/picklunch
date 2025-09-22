package golunch.repository;

import golunch.model.entity.LunchPicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LunchPickerRepo extends JpaRepository<LunchPicker, Long> {

    List<LunchPicker> findByStateNot(LunchPicker.State state);
    long countByStateNot(LunchPicker.State state);

}
