package com.perso.ecomm.review;

import com.perso.ecomm.playLoad.request.ReviewRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("review")
public class ReviewController {

    final private ReviewService reviewService;


    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long reviewId) {
            Review review = reviewService.getReviewById(reviewId);
            return ResponseEntity.ok(review);
    }

    @PostMapping("store")
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
            Review review = reviewService.storeReview(request);
            return ResponseEntity.ok(review);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReviewById(@PathVariable Long reviewId) {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("has been deleted");
    }

}
