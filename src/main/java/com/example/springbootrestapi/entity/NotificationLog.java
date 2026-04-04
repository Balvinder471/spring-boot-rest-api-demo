package com.example.springbootrestapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long userId;

    // Status codes: "P"=pending, "C"=confirmed, "S"=shipped, "D"=delivered, "X"=cancelled
    private String fromStatus;
    private String toStatus;

    @Column(length = 500)
    private String message;

    private LocalDateTime timestamp;

    public NotificationLog() {}

    public NotificationLog(Long orderId, Long userId, String fromStatus, String toStatus, String message) {
        this.orderId    = orderId;
        this.userId     = userId;
        this.fromStatus = fromStatus;
        this.toStatus   = toStatus;
        this.message    = message;
        this.timestamp  = LocalDateTime.now();
    }

    public Long getId()              { return id; }
    public Long getOrderId()         { return orderId; }
    public Long getUserId()          { return userId; }
    public String getFromStatus()    { return fromStatus; }
    public String getToStatus()      { return toStatus; }
    public String getMessage()       { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setId(Long id)                   { this.id = id; }
    public void setOrderId(Long orderId)         { this.orderId = orderId; }
    public void setUserId(Long userId)           { this.userId = userId; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public void setToStatus(String toStatus)     { this.toStatus = toStatus; }
    public void setMessage(String message)       { this.message = message; }
    public void setTimestamp(LocalDateTime ts)   { this.timestamp = ts; }
}
