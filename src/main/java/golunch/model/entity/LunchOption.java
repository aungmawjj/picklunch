package golunch.model.entity;

import jakarta.persistence.*;
import lombok.*;
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
    @Column(nullable = false)
    private String username;

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
