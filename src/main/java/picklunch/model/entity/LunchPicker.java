package picklunch.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LunchPicker {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "lunch_picker_id")
    private List<LunchOption> lunchOptions;

    @Enumerated(EnumType.STRING)
    private State state;

    private ZonedDateTime startTime;

    private Duration waitTime;

    @OneToOne
    @JoinColumn(name = "picked_lunch_option_id")
    private LunchOption pickedLunchOption;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private ZonedDateTime createdAt;

    @LastModifiedDate
    private ZonedDateTime updatedAt;

    public enum State {
        SUBMITTING, READY_TO_PICK, PICKED
    }

}
