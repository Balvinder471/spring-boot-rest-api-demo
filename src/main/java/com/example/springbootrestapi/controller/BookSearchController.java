package com.example.springbootrestapi.controller;

import com.example.springbootrestapi.dto.BookSearchResponse;
import com.example.springbootrestapi.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * BOOKS-101: Advanced Book Search & Filtering API with Pagination and Sorting.
 *
 * Fully implements all acceptance criteria:
 *   AC#1 - keyword search (title OR author, case-insensitive partial match)
 *   AC#2 - price range filter (minPrice / maxPrice, inclusive)
 *   AC#3 - all parameters combinable in a single request
 *   AC#4 - pagination via `page` and `size` params (default: page=0, size=10)
 *   AC#5 - sorting via `sortBy` (title|author|price) and `direction` (asc|desc)
 *   AC#6 - response envelope: data, totalElements, totalPages, currentPage, pageSize, hasNext, hasPrevious
 *   AC#7 - HTTP 400 when minPrice > maxPrice; empty data list (not 404) for no results
 */
@RestController
@RequestMapping("/api/books")
public class BookSearchController {

    @Autowired
    private BookService bookService;

    /**
     * Search and filter books.
     *
     * Examples:
     *   GET /api/books/search                                     → all books, page 0, size 10, sorted by title asc
     *   GET /api/books/search?q=clean                            → books with "clean" in title or author
     *   GET /api/books/search?minPrice=10&maxPrice=50            → books priced between £10 and £50
     *   GET /api/books/search?q=java&minPrice=20&maxPrice=100    → combined keyword + price filter
     *   GET /api/books/search?q=martin&sortBy=price&direction=desc&page=0&size=5
     */
    @GetMapping("/search")
    public ResponseEntity<BookSearchResponse> search(
            // AC#1: optional keyword for title/author search
            @RequestParam(required = false) String q,
            // AC#2: optional price range bounds
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            // AC#4: pagination params with defaults
            @RequestParam(defaultValue = "0")    int    page,
            @RequestParam(defaultValue = "10")   int    size,
            // AC#5: sorting params with defaults
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc")   String direction) {

        // AC#7: Validate page/size inputs
        if (page < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (size < 1 || size > 100) {
            return ResponseEntity.badRequest().build();
        }

        // AC#7: minPrice > maxPrice validation is handled inside BookService
        // and surfaces as IllegalArgumentException → caught by GlobalExceptionHandler → 400
        BookSearchResponse response = bookService.searchBooks(q, minPrice, maxPrice, page, size, sortBy, direction);

        // AC#7: No results → return 200 with empty data list (not 404)
        return ResponseEntity.ok(response);
    }
}
