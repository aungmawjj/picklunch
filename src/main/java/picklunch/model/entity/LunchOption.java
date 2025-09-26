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

import java.time.ZonedDateTime;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LunchOption {

    @Id
    @GeneratedValue
    private Long id;

    // having separate field to decouple from audit field
    @Column(name = "submitted_username", nullable = false)
    private String submittedUsername;

    @OneToOne
    @JoinColumn(
            name = "submitted_username",
            referencedColumnName = "username",
            insertable = false,
            updatable = false
    )
    private User submitter;

    @Column(nullable = false)
    private String shopName;

    private String shopUrl;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private ZonedDateTime createdAt;

    @LastModifiedDate
    private ZonedDateTime updatedAt;

}
