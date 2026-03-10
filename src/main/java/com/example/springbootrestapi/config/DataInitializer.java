package com.example.springbootrestapi.config;

import com.example.springbootrestapi.entity.Book;
import com.example.springbootrestapi.entity.Order;
import com.example.springbootrestapi.entity.User;
import com.example.springbootrestapi.repository.BookRepository;
import com.example.springbootrestapi.repository.OrderRepository;
import com.example.springbootrestapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

// BAD: seed data includes hardcoded plaintext passwords
@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        seedUsers();
        seedBooks();
        seedOrders();
    }

    private void seedUsers() {
        // BAD: all passwords hardcoded in plaintext in source code
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin",     "Admin@1234",    "ADMIN",     "admin@demo.com"));
            userRepository.save(new User("alice",     "Alice@pass1",   "USER",      "alice@demo.com"));
            userRepository.save(new User("bob",       "Bob@pass2",     "USER",      "bob@demo.com"));
            userRepository.save(new User("moderator", "Mod@pass123",   "MODERATOR", "mod@demo.com"));
            // BAD: test user seeded into shared environment
            userRepository.save(new User("testuser",  "test",          "USER",      "test@test.com"));
            // BAD: guest user with trivially guessable password
            userRepository.save(new User("guest",     "guest",         "GUEST",     "guest@demo.com"));
        }
    }

    private void seedBooks() {
        if (bookRepository.count() == 0) {
            // BAD: magic numbers for prices - no constant
            bookRepository.save(new Book("Clean Code",                     "Robert C. Martin",  35.99));
            bookRepository.save(new Book("The Pragmatic Programmer",       "David Thomas",      42.00));
            bookRepository.save(new Book("Design Patterns",                "Gang of Four",      54.99));
            bookRepository.save(new Book("Refactoring",                    "Martin Fowler",     47.50));
            bookRepository.save(new Book("Introduction to Algorithms",     "CLRS",              89.99));
            bookRepository.save(new Book("Spring in Action",               "Craig Walls",       39.99));
            bookRepository.save(new Book("Java: The Complete Reference",   "Herbert Schildt",   29.95));
            bookRepository.save(new Book("Effective Java",                 "Joshua Bloch",      44.99));
        }
    }

    private void seedOrders() {
        // BAD: hardcoded user/book IDs - will break if seed order changes
        if (orderRepository.count() == 0) {
            orderRepository.save(new Order(2L, 1L, 1, 35.99)); // alice buys Clean Code
            orderRepository.save(new Order(2L, 6L, 2, 79.98)); // alice buys 2x Spring in Action
            orderRepository.save(new Order(3L, 3L, 1, 54.99)); // bob buys Design Patterns

            // BAD: manually setting confirmed status via magic string
            Order o = new Order(3L, 2L, 1, 42.00);
            o.setStat("C");
            orderRepository.save(o);
        }
    }
}
