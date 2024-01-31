package com.perso.ecomm.review;

import com.perso.ecomm.product.Product;
import com.perso.ecomm.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "product_id",foreignKey = @ForeignKey(name = "fk_review_product"))
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id",foreignKey = @ForeignKey(name = "fk_review_user"))
    private User user;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 500)
    private String comment;

    @CreationTimestamp
    private Date reviewDate;


    public Review(Product product, User user, int rating, String comment) {
        this.product = product;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

    public Review() {

    }
}
