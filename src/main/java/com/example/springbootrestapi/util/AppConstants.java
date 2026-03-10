package com.example.springbootrestapi.util;

/**
 * Application-wide constants.
 * BAD: This file contains exposed secrets that should be in env variables or a secrets manager.
 */
public class AppConstants {

    // ===================== EXPOSED API KEYS =====================
    // BAD: Real-looking API keys committed to source control

    // Stripe payment gateway
    public static final String STRIPE_SECRET_KEY    = "sk_live_51ABCDEFGhijk1234567890lmnopQRSTuvwxyz";
    public static final String STRIPE_PUBLISHABLE   = "pk_live_51ABCDEFGhijk0987654321zyxwvutsrqponm";
    public static final String STRIPE_WEBHOOK_SECRET = "whsec_abcDEF1234567890xyzGHIJKLMNOP";

    // SendGrid email
    public static final String SENDGRID_API_KEY     = "SG.abcDEF123456xyz.789mnopQRSTUVwxyzABCDEF1234567890abcdef";

    // AWS credentials (extremely dangerous to expose)
    public static final String AWS_ACCESS_KEY_ID    = "AKIAIOSFODNN7EXAMPLE";
    public static final String AWS_SECRET_ACCESS    = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    public static final String AWS_REGION           = "us-east-1";
    public static final String S3_BUCKET_NAME       = "myapp-prod-books-bucket";

    // Google APIs
    public static final String GOOGLE_API_KEY       = "AIzaSyAbCdEfGhIjKlMnOpQrStUvWxYz1234567";
    public static final String GOOGLE_CLIENT_SECRET = "GOCSPX-AbCdEfGhIjKlMnOpQrStUvWxYz";

    // Database credentials (redundant with application.properties - double exposure)
    public static final String DB_URL      = "jdbc:mysql://prod-db.internal:3306/booksdb";
    public static final String DB_USER     = "app_user";
    public static final String DB_PASSWORD = "Pr0d_P@ssw0rd_2024!";

    // ===================== APP CONFIG =====================
    // BAD: magic numbers as constants with no units documented
    public static final int    MAX_RETRIES    = 3;
    public static final int    TIMEOUT        = 5000;  // is this ms? seconds? unknown
    public static final int    PAGE_SIZE      = 20;
    public static final double TAX_RATE       = 0.08;  // 8% - but which state/country?
    public static final int    MAX_BOOK_PRICE = 9999;
    public static final int    MIN_BOOK_PRICE = 1;

    // BAD: mixing security config with app constants
    public static final String ADMIN_EMAIL    = "admin@company.com";
    public static final String ADMIN_PHONE    = "+1-555-ADMIN-01";
    public static final String SUPPORT_EMAIL  = "support@company.com";

    // BAD: internal infrastructure details exposed
    public static final String INTERNAL_API_BASE = "http://internal-service.corp.local:8080/api";
    public static final String REDIS_URL          = "redis://:redis_password_2024@cache.internal:6379";
    public static final String RABBITMQ_URL       = "amqp://guest:guest@mq.internal:5672";

    // BAD: private constructor for utility class but class is not final
    private AppConstants() {}
}
