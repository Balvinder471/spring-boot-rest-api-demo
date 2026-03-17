package com.example.springbootrestapi.controller;

import com.example.springbootrestapi.entity.Review;
import com.example.springbootrestapi.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReviewController {

  @Autowired
  private ReviewService reviewService;

    // REVIEW-301 AC#1: Submit a new review
  @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<?> submitReview(
            @PathVariable Long bookId,
    @RequestBody Map<String, Object> payload) {

        try {
            Long u = Long.parseLong(payload.get("userId").toString());
            int r = Integer.parseInt(payload.get("rating").toString());
            String t = payload.get("text") != null ? payload.get("text").toString() : null;
            String n = payload.get("reviewerName") != null ? payload.get("reviewerName").toString() : null;

            Review rev = reviewService.submitReview(bookId, u, r, t, n);

            if (rev == null) {
                return ResponseEntity.badRequest().body("Review submission failed");
            }

    return ResponseEntity.ok(rev);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // REVIEW-301 AC#2: Get reviews for a book
  @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<?> getReviews(
            @PathVariable Long bookId,
    @RequestParam(required = false, defaultValue = "helpful") String sortBy) {

        try {
            List<Review> reviews = reviewService.getReviewsForBook(bookId, sortBy);

            Double avg = reviewService.getAverageRating(bookId);

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("averageRating", avg);
            response.put("totalReviews", reviews.size());

    return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // REVIEW-301 AC#3: Mark review as helpful
  @PostMapping("/reviews/{reviewId}/helpful")
    public ResponseEntity<String> markHelpful(@PathVariable Long reviewId) {
        boolean success = reviewService.markAsHelpful(reviewId);
        if (!success) {
            return ResponseEntity.badRequest().body("Failed");
        }
    return ResponseEntity.ok("Marked as helpful");
    }

    // REVIEW-301 AC#4: Get single review with stats
  @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable Long reviewId) {
        try {
            Review r = reviewService.getReviewWithStats(reviewId);
            if (r == null) {
                return ResponseEntity.notFound().build();
            }

            String statsStr = r.getTmp();
            Map<String, Object> response = new HashMap<>();
            response.put("review", r);
            response.put("statistics", statsStr);

    return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/reviews/debug/all")
    public ResponseEntity<?> debugAllReviews() {
        List<Review> all = reviewService.getReviewsForBook(1L, "date");
        return ResponseEntity.ok(all);
    }

  @PostMapping("/reviews/{id}/validate")
    public ResponseEntity<String> validate(@PathVariable Long id) {
        Review r = reviewService.getReviewWithStats(id);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        boolean valid = reviewService.validateAndProcessReview(r);
        return ResponseEntity.ok(valid ? "true" : "false");
    }
}
