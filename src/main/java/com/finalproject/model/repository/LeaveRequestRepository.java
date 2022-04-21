package com.finalproject.model.repository;

import com.finalproject.model.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository that provides access to activity request entity mapping in database
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByLeaveIdAndUserId(Long leaveId, Long userId);
}
