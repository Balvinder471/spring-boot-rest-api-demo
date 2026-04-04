package com.example.springbootrestapi.service;

import com.example.springbootrestapi.entity.NotificationLog;
import com.example.springbootrestapi.entity.Order;
import com.example.springbootrestapi.repository.NotificationLogRepository;
import com.example.springbootrestapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * NOTIF-402: Handles order status transitions — validates the transition,
 * persists the status change, builds the appropriate customer message,
 * and writes a NotificationLog audit entry.
 *
 * Covers AC#1 through AC#6.
 */
@Service
public class NotificationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    /**
     * NOTIF-402 AC#1–AC#4: Process a status transition for an order.
     * Validates the transition, updates the order, builds a notification
     * message (including refund amount for cancellations), and persists
     * a NotificationLog entry.
     *
     * @param orderId   the order to transition
     * @param newStatus the target status code
     * @return the saved NotificationLog, or null if transition is invalid
     */
    public NotificationLog processStatusChange(Long orderId, String newStatus) {
        Optional<Order> opt = orderRepository.findById(orderId);
        if (opt.isEmpty()) {
            return null;
        }

        Order order     = opt.get();
        String fromStatus = order.getStat();

        // --- Validate the transition (mirrors OrderService.updateOrderStatus logic) ---
        boolean validTransition = isValidTransition(fromStatus, newStatus);
        if (!validTransition) {
            return null;
        }

        // --- Persist the status change on the Order ---
        order.setStat(newStatus);
        orderRepository.save(order);

        // --- Build the notification message based on the transition ---
        String message = buildMessage(order, fromStatus, newStatus);

        // --- AC#1: Persist the audit log entry ---
        NotificationLog log = new NotificationLog(
                orderId, order.getUserId(), fromStatus, newStatus, message);
        return notificationLogRepository.save(log);
    }

    /**
     * Validates whether a status transition is permitted.
     * P → C, P → X
     * C → S, C → X
     * S → D, S → X
     * D → (nothing)
     * X → (nothing)
     */
    private boolean isValidTransition(String from, String to) {
        if (from == null || to == null) return false;
        return switch (from) {
            case "P" -> to.equals("C") || to.equals("X");
            case "C" -> to.equals("S") || to.equals("X");
            case "S" -> to.equals("D") || to.equals("X");
            default  -> false;
        };
    }

    /**
     * NOTIF-402 AC#2, AC#3, AC#4: Builds the customer-facing notification message
     * for the given transition.
     *
     * ⚠️ BUG (AC#4 — SHIPPED cancellation):
     *   The refund logic handles PENDING (100%) and CONFIRMED (95%) correctly.
     *   However, the SHIPPED → CANCELLED branch is MISSING entirely.
     *   When an order in SHIPPED status is cancelled, it falls through to the
     *   generic else-clause which:
     *     (a) applies 95% refund (CONFIRMED rate) instead of the required 90%
     *     (b) omits the refund amount from the message
     *   This will only surface in production when a customer cancels an already-shipped order.
     */
    private String buildMessage(Order order, String fromStatus, String toStatus) {
        long orderId = order.getId();

        // AC#2: Confirmed → Shipped
        if (fromStatus.equals("C") && toStatus.equals("S")) {
            return String.format(
                "Your order #%d has been shipped! Estimated delivery in 3-5 business days.", orderId);
        }

        // AC#3: Shipped → Delivered
        if (fromStatus.equals("S") && toStatus.equals("D")) {
            return String.format(
                "Your order #%d has been delivered. Thank you for shopping with us!", orderId);
        }

        // AC#4: Any → Cancelled
        if (toStatus.equals("X")) {
            double totalPrice = order.getTp();

            // Cancelled from PENDING → 100% refund
            if (fromStatus.equals("P")) {
                double refund = totalPrice;
                return String.format(
                    "Your order #%d has been cancelled. Full refund of $%.2f will be processed.", orderId, refund);
            }

            // Cancelled from CONFIRMED → 95% refund (5% admin fee)
            if (fromStatus.equals("C")) {
                double refund = totalPrice * 0.95;
                return String.format(
                    "Your order #%d has been cancelled. Refund of $%.2f will be processed (5%% admin fee applied).",
                    orderId, refund);
            }

            // ❌ BUG: The SHIPPED → CANCELLED branch is completely missing here.
            // AC#4 requires: refund = totalPrice * 0.90 (10% restocking fee)
            // and message must include the refund amount.
            //
            // The correct code that was NEVER written:
            //
            //   if (fromStatus.equals("S")) {
            //       double refund = totalPrice * 0.90;
            //       return String.format(
            //           "Your order #%d has been cancelled. Refund of $%.2f will be processed " +
            //           "(10%% restocking fee applied as item was already shipped).", orderId, refund);
            //   }
            //
            // Because this branch is absent, a SHIPPED cancellation falls through to the
            // generic fallback below, giving the wrong refund rate and a vague message.

            // Generic fallback — wrong for SHIPPED, no refund amount shown
            double refund = totalPrice * 0.95;   // ← wrong: should be 0.90 for S→X
            return String.format("Your order #%d has been cancelled. Refund will be processed.", orderId);
        }

        // P → C (order confirmed)
        if (fromStatus.equals("P") && toStatus.equals("C")) {
            return String.format("Your order #%d has been confirmed and is being prepared.", orderId);
        }

        // Fallback for any unhandled transition
        return String.format("Order #%d status updated from %s to %s.", orderId, fromStatus, toStatus);
    }

    /**
     * NOTIF-402 AC#6: Retrieve all notification log entries for a given order,
     * in reverse-chronological order.
     */
    public List<NotificationLog> getNotificationsForOrder(Long orderId) {
        return notificationLogRepository.findByOrderIdOrderByTimestampDesc(orderId);
    }
}
