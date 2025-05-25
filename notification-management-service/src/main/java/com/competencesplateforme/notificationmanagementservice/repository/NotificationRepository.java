package com.competencesplateforme.notificationmanagementservice.repository;


import com.competencesplateforme.notificationmanagementservice.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find with pagination
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find notifications created after a specific date
    List<Notification> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);

    // Find notifications by title containing text (case insensitive)
    Page<Notification> findByTitreContainingIgnoreCaseOrderByCreatedAtDesc(String titre, Pageable pageable);

    // Find notification with user notifications eagerly loaded
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.userNotifications WHERE n.id = :id")
    Optional<Notification> findByIdWithUserNotifications(@Param("id") Long id);

    // Find recent notifications (last N days)
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :startDate ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("startDate") LocalDateTime startDate);


}
