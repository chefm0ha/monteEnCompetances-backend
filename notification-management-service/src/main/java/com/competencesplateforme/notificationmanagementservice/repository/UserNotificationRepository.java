package com.competencesplateforme.notificationmanagementservice.repository;

import com.competencesplateforme.notificationmanagementservice.dto.NotificationSummaryDto;
import com.competencesplateforme.notificationmanagementservice.model.UserNotification;
import com.competencesplateforme.notificationmanagementservice.model.UserNotificationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UserNotificationId> {

    // Find by composite key
    Optional<UserNotification> findByUserIdAndNotificationId(UUID userId, Long notificationId);

    // Find all notifications for a user with pagination
    @Query("SELECT un FROM UserNotification un JOIN FETCH un.notification n WHERE un.userId = :userId ORDER BY n.createdAt DESC")
    Page<UserNotification> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId, Pageable pageable);

    // Find unseen notifications for a user
    @Query("SELECT un FROM UserNotification un JOIN FETCH un.notification n WHERE un.userId = :userId AND un.seenAt IS NULL ORDER BY n.createdAt DESC")
    List<UserNotification> findUnseenByUserId(@Param("userId") UUID userId);

    // Count unseen notifications for a user
    @Query("SELECT COUNT(un) FROM UserNotification un WHERE un.userId = :userId AND un.seenAt IS NULL")
    Long countUnseenByUserId(@Param("userId") UUID userId);

    // Mark specific notification as seen
    @Modifying
    @Query("UPDATE UserNotification un SET un.seenAt = :seenAt WHERE un.userId = :userId AND un.notificationId = :notificationId AND un.seenAt IS NULL")
    int markAsSeen(@Param("userId") UUID userId, @Param("notificationId") Long notificationId, @Param("seenAt") LocalDateTime seenAt);

    // Mark all notifications as seen for a user
    @Modifying
    @Query("UPDATE UserNotification un SET un.seenAt = :seenAt WHERE un.userId = :userId AND un.seenAt IS NULL")
    int markAllAsSeen(@Param("userId") UUID userId, @Param("seenAt") LocalDateTime seenAt);

    // Get notification summary for a user (optimized query)
    @Query("SELECT new com.competencesplateforme.notificationmanagementservice.dto.NotificationSummaryDto(" +
            "n.id, n.titre, n.contenu, n.createdAt, un.seenAt) " +
            "FROM UserNotification un JOIN un.notification n " +
            "WHERE un.userId = :userId ORDER BY n.createdAt DESC")
    Page<NotificationSummaryDto> findNotificationSummaryByUserId(@Param("userId") UUID userId, Pageable pageable);

    // Find users who received a specific notification
    @Query("SELECT un FROM UserNotification un WHERE un.notificationId = :notificationId")
    List<UserNotification> findByNotificationId(@Param("notificationId") Long notificationId);

    // Check if user has notification
    boolean existsByUserIdAndNotificationId(UUID userId, Long notificationId);
}
