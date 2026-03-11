package com.example.springbootrestapi.dto;

import com.example.springbootrestapi.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * BOOKS-101: Paginated response envelope for book search results.
 * Satisfies AC#6: includes data, totalElements, totalPages, currentPage, pageSize, hasNext, hasPrevious.
 */
public class BookSearchResponse {

    private List<Book> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    // Factory method to build from a Spring Data Page
    public static BookSearchResponse from(Page<Book> page) {
        BookSearchResponse response = new BookSearchResponse();
        response.data          = page.getContent();
        response.totalElements = page.getTotalElements();
        response.totalPages    = page.getTotalPages();
        response.currentPage   = page.getNumber();
        response.pageSize      = page.getSize();
        response.hasNext       = page.hasNext();
        response.hasPrevious   = page.hasPrevious();
        return response;
    }

    public List<Book> getData()          { return data; }
    public long getTotalElements()       { return totalElements; }
    public int getTotalPages()           { return totalPages; }
    public int getCurrentPage()          { return currentPage; }
    public int getPageSize()             { return pageSize; }
    public boolean isHasNext()           { return hasNext; }
    public boolean isHasPrevious()       { return hasPrevious; }
}
