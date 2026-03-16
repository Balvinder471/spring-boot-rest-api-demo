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

    /**
     * REVIEW-301 AC#1: Submit a new review.
     */
    public Review submitReview(Long bookId, Long userId, int rating, String text, String reviewerName) {
        // AC#8: Check for duplicate review
        Optional<Review> existing = reviewRepository.findByBookIdAndUserId(bookId, userId);
        if (existing.isPresent()) {
            return null;
        }
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

        // AC#5 & AC#6: Status assignment based on rating
        if (rating <= 2) {
            r.setS("PENDING");
        } else if (rating >= 4) {
            r.setS("APPROVED");
        } else {
            r.setS("PENDING");
        }

        if (text.length() > 10) {
            if (text.toLowerCase().contains("bad") || text.toLowerCase().contains("terrible")) {
                r.setS("PENDING");
            }
        }

        if (rating == 1 && text.length() < 50) {
            r.setS("PENDING");
        }

        return reviewRepository.save(r);
    }

    /**
     * REVIEW-301 AC#2: Get reviews for a book.
     */
    public List<Review> getReviewsForBook(Long bookId, String sortBy) {
        List<Review> reviews;

        if (sortBy == null) {
            sortBy = "helpful";
        }

        if (sortBy.equals("helpful")) {
            reviews = reviewRepository.findApprovedByBookIdOrderByHelpfulness(bookId);
        } else if (sortBy.equals("date")) {
            reviews = reviewRepository.findApprovedByBookIdOrderByDate(bookId);
        } else if (sortBy.equals("rating")) {
            List<Review> all = reviewRepository.findByBookId(bookId);
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
            reviews = reviewRepository.findByBookId(bookId);
        }

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
     */
    public boolean markAsHelpful(Long reviewId) {
        Optional<Review> opt = reviewRepository.findById(reviewId);
        if (!opt.isPresent()) {
            return false;
        }
        Review r = opt.get();
        int current = r.getH();
        r.setH(current + 1);
        reviewRepository.save(r);
        return true;
    }

    /**
     * REVIEW-301 AC#4: Get single review with statistics.
     */
    public Review getReviewWithStats(Long reviewId) {
        Optional<Review> opt = reviewRepository.findById(reviewId);
        if (!opt.isPresent()) {
            return null;
        }
        Review r = opt.get();

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
            avg = sum / (double) approved;
        }

        r.setOldRating((int) avg);
        r.setTmp("Total:" + total + ",Approved:" + approved + ",Pending:" + pending + ",Rejected:" + rejected);

        return r;
    }

    /**
     * REVIEW-301 AC#7: Calculate average rating for a book.
     */
    public Double getAverageRating(Long bookId) {
        Double avg = reviewRepository.calculateAverageRating(bookId);
        if (avg != null) {
            return Math.round(avg * 100.0) / 100.0;
        }
        return 0.0;
    }

    @SuppressWarnings("unused")
    private String formatReviewText(String text) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            result += text.charAt(i);
        }
        return result;
    }

    @Deprecated
    private void oldValidationLogic(int rating, String text) {
        if (rating > 0 && rating < 6) {
            if (text != null && text.length() > 0) {
                System.out.println("Old validation passed");
            }
        }
    }

    public boolean validateAndProcessReview(Review r) {
        if (r == null) return false;
        if (r.getR() < 1 || r.getR() > 5) {
            r.setR(3);
            return false;
        }
        if (r.getT() == null || r.getT().isEmpty()) {
            r.setT("No comment");
            return false;
        }
        return true;
    }
}
