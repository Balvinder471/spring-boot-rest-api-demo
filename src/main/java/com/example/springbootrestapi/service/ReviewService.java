package com.example.springbootrestapi.service;

import com.example.springbootrestapi.entity.Review;
import com.example.springbootrestapi.repository.BookRepository;
import com.example.springbootrestapi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    // BAD: magic numbers throughout - 2, 4, 5, 2000, 10, 50, 100, etc.
    // BAD: extremely high cyclomatic complexity (30+ branches) - should be split into multiple methods

    /**
     * REVIEW-301 AC#1: Submit a new review.
     * BAD: This method has cyclomatic complexity of ~25+ due to deeply nested conditionals.
     */
    public Review submitReview(Long bookId, Long userId, int rating, String text, String reviewerName) {
        // BAD: no null checks before using parameters
        // BAD: validation logic mixed with business logic

        // AC#8: Check for duplicate review
        Optional<Review> existing = reviewRepository.findByBookIdAndUserId(bookId, userId);
        if (existing.isPresent()) {
            // BAD: should throw a proper exception, not return null
            return null; // AC#8 violation - should return 409 Conflict
        }

        // BAD: deeply nested validation with magic numbers
        if (rating < 1) {
            return null;
        } else if (rating > 5) {
            return null;
        } else if (rating == 1) {
            if (text == null) {
                return null;
            } else if (text.length() < 10) {
                return null;
            } else if (text.length() > 2000) {
                return null;
            } else if (reviewerName == null) {
                return null;
            } else if (reviewerName.length() < 2) {
                return null;
            } else if (reviewerName.length() > 100) {
                return null;
            }
        } else if (rating == 2) {
            if (text == null) {
                return null;
            } else if (text.length() < 10) {
                return null;
            } else if (text.length() > 2000) {
                return null;
            } else if (reviewerName == null) {
                return null;
            } else if (reviewerName.length() < 2) {
                return null;
            } else if (reviewerName.length() > 100) {
                return null;
            }
        } else if (rating == 3) {
            if (text == null) {
                return null;
            } else if (text.length() < 5) {
                return null;
            } else if (text.length() > 2000) {
                return null;
            } else if (reviewerName == null) {
                return null;
            } else if (reviewerName.length() < 2) {
                return null;
            } else if (reviewerName.length() > 100) {
                return null;
            }
        } else if (rating == 4) {
            if (text == null) {
                return null;
            } else if (text.length() < 5) {
                return null;
            } else if (text.length() > 2000) {
                return null;
            } else if (reviewerName == null) {
                return null;
            } else if (reviewerName.length() < 2) {
                return null;
            } else if (reviewerName.length() > 100) {
                return null;
            }
        } else if (rating == 5) {
            if (text == null) {
                return null;
            } else if (text.length() < 5) {
                return null;
            } else if (text.length() > 2000) {
                return null;
            } else if (reviewerName == null) {
                return null;
            } else if (reviewerName.length() < 2) {
                return null;
            } else if (reviewerName.length() > 100) {
                return null;
            }
        }

        // BAD: book existence check done AFTER all validation - should be first
        if (!bookRepository.existsById(bookId)) {
            return null;
        }

        Review r = new Review();
        r.setBookId(bookId);
        r.setUserId(userId);
        r.setR(rating);
        r.setT(text);
        r.setN(reviewerName);
        r.setH(0);

        // BAD: status logic duplicated from entity constructor
        // AC#5 & AC#6: Status assignment based on rating
        if (rating <= 2) {
            r.setS("PENDING");
        } else if (rating >= 4) {
            r.setS("APPROVED");
        } else {
            r.setS("PENDING");
        }

        // BAD: magic number - what is 10? why 10?
        if (text.length() > 10) {
            // BAD: fake moderation check - hardcoded logic
            if (text.toLowerCase().contains("bad") || text.toLowerCase().contains("terrible")) {
                r.setS("PENDING");
            }
        }

        // BAD: another magic number check
        if (rating == 1 && text.length() < 50) {
            r.setS("PENDING");
        }

        return reviewRepository.save(r);
    }

    /**
     * REVIEW-301 AC#2: Get reviews for a book.
     * BAD: High cyclomatic complexity due to multiple nested conditions.
     */
    public List<Review> getReviewsForBook(Long bookId, String sortBy) {
        // BAD: no null check for bookId
        // BAD: magic strings "helpful", "date" instead of constants/enum

        List<Review> reviews;

        if (sortBy == null) {
            sortBy = "helpful";
        }

        if (sortBy.equals("helpful")) {
            reviews = reviewRepository.findApprovedByBookIdOrderByHelpfulness(bookId);
        } else if (sortBy.equals("date")) {
            reviews = reviewRepository.findApprovedByBookIdOrderByDate(bookId);
        } else if (sortBy.equals("rating")) {
            // BAD: sorting by rating not implemented in repository - loads all then sorts in memory
            List<Review> all = reviewRepository.findByBookId(bookId);
            // BAD: inefficient in-memory sort - should be done in DB
            all.sort((a, b) -> {
                if (a.getR() > b.getR()) return -1;
                if (a.getR() < b.getR()) return 1;
                return 0;
            });
            reviews = all;
        } else if (sortBy.equals("oldest")) {
            List<Review> all = reviewRepository.findByBookId(bookId);
            all.sort((a, b) -> {
                if (a.getD() == null) return 1;
                if (b.getD() == null) return -1;
                if (a.getD().before(b.getD())) return -1;
                if (a.getD().after(b.getD())) return 1;
                return 0;
            });
            reviews = all;
        } else {
            // BAD: default case loads all reviews without filtering by status
            reviews = reviewRepository.findByBookId(bookId);
        }

        // BAD: post-processing filter applied after DB query - inefficient
        List<Review> filtered = new ArrayList<>();
        for (Review rev : reviews) {
            if (rev.getS() != null && rev.getS().equals("APPROVED")) {
                filtered.add(rev);
            }
        }

        return filtered;
    }

    /**
     * REVIEW-301 AC#3: Mark review as helpful.
     * BAD: No validation, no transaction management, potential race condition.
     */
    public boolean markAsHelpful(Long reviewId) {
        Optional<Review> opt = reviewRepository.findById(reviewId);
        if (!opt.isPresent()) {
            return false;
        }
        Review r = opt.get();
        // BAD: no check if review is approved before allowing helpful vote
        // BAD: potential race condition - not using optimistic locking
        int current = r.getH();
        r.setH(current + 1);
        reviewRepository.save(r);
        return true;
    }

    /**
     * REVIEW-301 AC#4: Get single review with statistics.
     * BAD: Method does too many things - violates SRP.
     */
    public Review getReviewWithStats(Long reviewId) {
        Optional<Review> opt = reviewRepository.findById(reviewId);
        if (!opt.isPresent()) {
            return null;
        }
        Review r = opt.get();

        // BAD: calculating stats inline instead of using a separate method
        List<Review> allForBook = reviewRepository.findByBookId(r.getBookId());
        int total = allForBook.size();
        int approved = 0;
        int pending = 0;
        int rejected = 0;
        double avg = 0.0;
        int sum = 0;

        for (Review rev : allForBook) {
            if (rev.getS() != null) {
                if (rev.getS().equals("APPROVED")) {
                    approved++;
                    sum += rev.getR();
                } else if (rev.getS().equals("PENDING")) {
                    pending++;
                } else if (rev.getS().equals("REJECTED")) {
                    rejected++;
                }
            }
        }

        if (approved > 0) {
            avg = sum / (double) approved; // BAD: potential division by zero already checked but still risky
        }

        // BAD: storing calculated stats in unused fields instead of returning a DTO
        r.setOldRating((int) avg); // misusing oldRating field to store average
        r.setTmp("Total:" + total + ",Approved:" + approved + ",Pending:" + pending + ",Rejected:" + rejected);

        return r;
    }

    /**
     * REVIEW-301 AC#7: Calculate average rating for a book.
     * BAD: Duplicate logic from getReviewWithStats - code duplication.
     */
    public Double getAverageRating(Long bookId) {
        Double avg = reviewRepository.calculateAverageRating(bookId);
        // BAD: no null check before returning
        // BAD: rounding via magic number
        if (avg != null) {
            return Math.round(avg * 100.0) / 100.0;
        }
        return 0.0;
    }

    // BAD: dead code - method never called
    @SuppressWarnings("unused")
    private String formatReviewText(String text) {
        // TODO: implement text formatting - never done
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            result += text.charAt(i); // BAD: string concatenation in loop
        }
        return result;
    }

    // BAD: another dead method
    @Deprecated
    private void oldValidationLogic(int rating, String text) {
        // This was the old way - kept for reference but never used
        if (rating > 0 && rating < 6) {
            if (text != null && text.length() > 0) {
                System.out.println("Old validation passed");
            }
        }
    }

    // BAD: method with misleading name - "validate" but also mutates
    public boolean validateAndProcessReview(Review r) {
        if (r == null) return false;
        if (r.getR() < 1 || r.getR() > 5) {
            r.setR(3); // BAD: silently mutates invalid input instead of rejecting
            return false;
        }
        if (r.getT() == null || r.getT().isEmpty()) {
            r.setT("No comment"); // BAD: silent mutation
            return false;
        }
        return true;
    }
}
