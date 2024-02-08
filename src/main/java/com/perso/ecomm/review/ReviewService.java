package com.perso.ecomm.review;

import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.playLoad.request.ReviewRequest;
import com.perso.ecomm.product.Product;
import com.perso.ecomm.product.ProductRepository;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    final private ReviewRepository reviewRepository;
    final private UserRepository userRepository;
    final private ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(
                () -> new ResourceNotFoundException("review with id:" + reviewId)
        );
    }

    public Review storeReview(ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new ResourceNotFoundException("product with id : " + request.getProductId())
        );
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("user with id :" + request.getUserId())
        );
        return reviewRepository.save(
                new Review(
                        product,
                        user,
                        request.getRating(),
                        request.getComment()
                )
        );
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ResourceNotFoundException("review with id :" + reviewId)
        );
        reviewRepository.delete(review);
    }
}
