package com.example.springbootrestapi.repository;

import com.example.springbootrestapi.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // AC#6: Fetch all logs for a given order, sorted newest-first
    List<NotificationLog> findByOrderIdOrderByTimestampDesc(Long orderId);

    List<NotificationLog> findByUserId(Long userId);
}
