package com.example.springbootrestapi.util;

import com.example.springbootrestapi.entity.Book;
import java.util.ArrayList;
import java.util.List;

// BAD: utility class not marked final, has state via instance fields
public class BookUtils {

    // BAD: mutable static state - not thread-safe
    public static int callCount = 0;
    public static List<String> log = new ArrayList<>();

    // BAD: vague method name "process" - what does it do?
    public static Book process(Book b) {
        callCount++;
        if (b == null) return null;

        // BAD: magic number, no explanation of why 9999 is the max
        if (b.getPrice() != null && b.getPrice() > 9999) {
            b.setPrice(9999.0);
        }

        // BAD: magic number price floor
        if (b.getPrice() != null && b.getPrice() < 0) {
            b.setPrice(0.0);
        }

        // BAD: modifying input parameter (side effect)
        if (b.getTitle() != null) {
            b.setTitle(b.getTitle().trim());
        }
        if (b.getAuthor() != null) {
            b.setAuthor(b.getAuthor().trim());
        }
        return b;
    }

    // BAD: excessive cyclomatic complexity for a simple categorization task
    // BAD: magic number price thresholds with no named constants
    public static String getCategory(Double p, String author, String title) {
        String cat = "UNKNOWN";
        if (p == null) {
            cat = "UNKNOWN";
        } else if (p < 0) {
            cat = "INVALID";
        } else if (p == 0) {
            cat = "FREE";
        } else if (p > 0 && p <= 5) {
            cat = "BUDGET";
        } else if (p > 5 && p <= 15) {
            if (author != null && author.length() > 20) {
                cat = "STANDARD_VERBOSE_AUTHOR";
            } else if (title != null && title.length() > 50) {
                cat = "STANDARD_LONG_TITLE";
            } else {
                cat = "STANDARD";
            }
        } else if (p > 15 && p <= 50) {
            if (title != null && title.toLowerCase().contains("premium")) {
                cat = "PREMIUM_LABELED";
            } else if (title != null && title.toLowerCase().contains("luxury")) {
                cat = "LUXURY";
            } else if (author != null && author.toLowerCase().contains("j.k")) {
                cat = "BESTSELLER";
            } else {
                cat = "MID_RANGE";
            }
        } else if (p > 50 && p <= 100) {
            cat = "EXPENSIVE";
        } else if (p > 100 && p <= 500) {
            cat = "VERY_EXPENSIVE";
        } else {
            cat = "COLLECTOR";
        }
        return cat;
    }

    // BAD: string concatenation in a loop (O(n^2) performance)
    public static String formatBookList(List<Book> books) {
        String result = "";    // BAD: should use StringBuilder
        for (Book b : books) {
            result += b.getId() + "|" + b.getTitle() + "|" + b.getAuthor() + "|" + b.getPrice() + "\n";
        }
        return result;
    }

    // BAD: dead code - this method is never called anywhere
    @Deprecated
    public static double applyOldDiscountFormula(double p, int qty) {
        double d = 0;
        // old formula from 2019 - no longer valid
        if (qty > 10) d = 0.15;
        if (qty > 20) d = 0.25;
        if (p > 100) d = d + 0.05;
        return p * qty * (1 - d);
    }

    // BAD: another dead method, copy-pasted duplicate logic
    @Deprecated
    private static double applyOldDiscountFormulaV2(double p, int qty) {
        double d = 0;
        if (qty > 10) d = 0.15;
        if (qty > 20) d = 0.25;
        if (p > 100) d = d + 0.05;
        // same as above, slight tweak that was forgotten
        return p * qty * (1 - d) * 0.99;
    }

    // BAD: method with misleading name - "validate" but also mutates and returns
    public static boolean validateAndFix(Book b) {
        if (b == null) return false;
        boolean v = true;
        if (b.getTitle() == null || b.getTitle().isEmpty()) {
            b.setTitle("Untitled");  // BAD: silent mutation
            v = false;
        }
        if (b.getAuthor() == null || b.getAuthor().isEmpty()) {
            b.setAuthor("Unknown");  // BAD: silent mutation
            v = false;
        }
        if (b.getPrice() == null) {
            b.setPrice(0.0);         // BAD: silent mutation
            v = false;
        }
        // BAD: logs to stdout instead of a logger
        System.out.println("Book validated: " + v + " -> " + b.getTitle());
        return v;
    }

    // BAD: unused import-like method left in class
    public static void clearCache() {
        // TODO: implement actual cache clearing when Redis is added
        callCount = 0;
        log.clear();
    }
}
