package dmit2015.estebangonzalez.assignment03.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "oscarReviews")

// @author: Esteban Gonzalez
// @version: February 4, 2022
public class OscarReview implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category field value is required.")
    @Column(nullable = false, length = 64)
    private String category;

    @NotBlank(message = "Nominee field value is required.")
    @Column(nullable = false, length = 32)
    private String nominee;

    @NotBlank(message = "Review field value is required.")
    @Column(nullable = false, length = 128)
    private String review;

    @NotBlank(message = "Username field value is required.")
    @Column(nullable = false, length = 32)
    private String username;

//    @Column(nullable = false)
//    private LocalDateTime createdDateTime;
//
//    @Column(nullable = false)
//    private LocalDateTime lastModifiedDateTime;
//
//    @PrePersist
//    private void beforePersist() {
//        createdDateTime = LocalDateTime.now();
//        lastModifiedDateTime = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    private void beforeUpdate() {
//        lastModifiedDateTime = LocalDateTime.now();
//    }
//
    private boolean complete;

    @Version
    private Integer version;

    public OscarReview(Long id, String category, String nominee, String review, String username, boolean complete) {
        this.id = id;
        this.category = category;
        this.nominee = nominee;
        this.review = review;
        this.username = username;
        this.complete = complete;
    }
}