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
  private ReviewService reviewService;  // BAD: inconsistent indentation

    // REVIEW-301 AC#1: Submit a new review
  @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<?> submitReview(
            @PathVariable Long bookId,
    @RequestBody Map<String, Object> payload) {  // BAD: using raw Map instead of DTO

        try {
            // BAD: vague variable names, unchecked casts
            Long u = Long.parseLong(payload.get("userId").toString());
            int r = Integer.parseInt(payload.get("rating").toString());
            String t = payload.get("text") != null ? payload.get("text").toString() : null;
            String n = payload.get("reviewerName") != null ? payload.get("reviewerName").toString() : null;

            // BAD: no validation before calling service
            Review rev = reviewService.submitReview(bookId, u, r, t, n);

            // BAD: returning null check instead of proper exception handling
            if (rev == null) {
                // BAD: AC#8 violation - should return 409 for duplicate, but returns 400 for everything
                return ResponseEntity.badRequest().body("Review submission failed");
            }

            // BAD: inconsistent spacing in return statement
    return ResponseEntity.ok(rev);
        } catch (Exception e) {
            // BAD: generic catch-all, no specific exception handling
            // BAD: empty catch would be worse, but this is still bad practice
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // REVIEW-301 AC#2: Get reviews for a book
  @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<?> getReviews(
            @PathVariable Long bookId,
    @RequestParam(required = false, defaultValue = "helpful") String sortBy) {  // BAD: inconsistent spacing

        try {
            // BAD: no validation that bookId exists
            List<Review> reviews = reviewService.getReviewsForBook(bookId, sortBy);

            // BAD: calculating average inline in controller - should be in service/DTO
            Double avg = reviewService.getAverageRating(bookId);

            // BAD: building response manually instead of using a DTO
            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("averageRating", avg);
            response.put("totalReviews", reviews.size());

            // BAD: inconsistent indentation
    return ResponseEntity.ok(response);
        } catch (Exception e) {
            // BAD: same generic exception handling
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // REVIEW-301 AC#3: Mark review as helpful
  @PostMapping("/reviews/{reviewId}/helpful")
    public ResponseEntity<String> markHelpful(@PathVariable Long reviewId) {
        // BAD: no validation, no error details
        boolean success = reviewService.markAsHelpful(reviewId);
        if (!success) {
            // BAD: vague error message
            return ResponseEntity.badRequest().body("Failed");
        }
        // BAD: inconsistent spacing
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

            // BAD: parsing stats from misused tmp field instead of proper DTO
            String statsStr = r.getTmp();
            Map<String, Object> response = new HashMap<>();
            response.put("review", r);
            response.put("statistics", statsStr); // BAD: returning string instead of structured data

            // BAD: inconsistent indentation
    return ResponseEntity.ok(response);
        } catch (Exception e) {
            // BAD: generic exception handling again
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // BAD: debug endpoint left in production code
    @GetMapping("/reviews/debug/all")
    public ResponseEntity<?> debugAllReviews() {
        // BAD: exposes all reviews including pending/rejected ones
        List<Review> all = reviewService.getReviewsForBook(1L, "date");
        return ResponseEntity.ok(all);
    }

    // BAD: method with vague name and no documentation
  @PostMapping("/reviews/{id}/validate")
    public ResponseEntity<String> validate(@PathVariable Long id) {
        // BAD: what does this validate? unclear method name
        Review r = reviewService.getReviewWithStats(id);
        if (r == null) {
            return ResponseEntity.notFound().build();
        }
        boolean valid = reviewService.validateAndProcessReview(r);
        // BAD: returning boolean as string
        return ResponseEntity.ok(valid ? "true" : "false");
    }
}
