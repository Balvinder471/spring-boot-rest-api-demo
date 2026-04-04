package com.example.springbootrestapi.controller;

import com.example.springbootrestapi.entity.NotificationLog;
import com.example.springbootrestapi.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NOTIF-402: New endpoints for order status transitions that go through the
 * full notification + audit pipeline.
 *
 * AC#5 COMPLIANCE NOTE:
 *   This controller correctly calls NotificationService for all transitions.
 *   However, the PRE-EXISTING endpoint in OrderController (PUT /api/orders/{id}/status)
 *   was NEVER updated to call NotificationService — it still calls the old
 *   OrderService.updateOrderStatus() which only changes the DB field with no
 *   audit log or notification. Any client still using that endpoint silently
 *   bypasses the entire NOTIF-402 feature (AC#5 broken).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderStatusController {

    @Autowired
    private NotificationService notificationService;

    /**
     * NOTIF-402 AC#1–AC#4: Process an order status transition through the full
     * notification pipeline (validates transition, updates status, builds message,
     * writes audit log).
     *
     * POST /api/orders/{id}/process-status?newStatus=C
     */
    @PostMapping("/{id}/process-status")
    public ResponseEntity<?> processStatusChange(@PathVariable Long id,
                                                 @RequestParam String newStatus) {
        NotificationLog log = notificationService.processStatusChange(id, newStatus);
        if (log == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid status transition or order not found");
        }
        return ResponseEntity.ok(log);
    }

    /**
     * NOTIF-402 AC#6: Retrieve all notification log entries for an order,
     * in reverse-chronological order.
     *
     * GET /api/orders/{id}/notifications
     */
    @GetMapping("/{id}/notifications")
    public ResponseEntity<List<NotificationLog>> getNotifications(@PathVariable Long id) {
        List<NotificationLog> logs = notificationService.getNotificationsForOrder(id);
        return ResponseEntity.ok(logs);
    }
}
