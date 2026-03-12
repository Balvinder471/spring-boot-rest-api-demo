package com.example.springbootrestapi.repository;

import com.example.springbootrestapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // BAD: no pagination - could return huge result set
    List<Review> findByBookId(Long bookId);

    // BAD: magic string "APPROVED" hardcoded in query
    @Query(value = "SELECT * FROM reviews WHERE book_id = ?1 AND s = 'APPROVED' ORDER BY h DESC", nativeQuery = true)
    List<Review> findApprovedByBookIdOrderByHelpfulness(Long bookId);

    // BAD: another magic string query
    @Query(value = "SELECT * FROM reviews WHERE book_id = ?1 AND s = 'APPROVED' ORDER BY d DESC", nativeQuery = true)
    List<Review> findApprovedByBookIdOrderByDate(Long bookId);

    // BAD: no index hint, could be slow on large datasets
    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);

    // BAD: native SQL with string concatenation risk
    @Query(value = "SELECT AVG(r) FROM reviews WHERE book_id = :bookId AND s = 'APPROVED'", nativeQuery = true)
    Double calculateAverageRating(@Param("bookId") Long bookId);

    // BAD: returns all reviews with a status - no pagination
    List<Review> findByS(String status);

    // BAD: count via loading all entities instead of COUNT query
    @Query(value = "SELECT * FROM reviews WHERE s = ?1", nativeQuery = true)
    List<Review> findAllByStatus(String status);
}
