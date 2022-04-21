package com.finalproject.model.service;

import com.finalproject.model.entity.*;
import com.finalproject.model.repository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;


/**
 * Service with business logic for managing leaves requests
 *
 */
@Log4j2
@Service
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, UserRepository userRepository, LeaveRepository leaveRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
        this.leaveRepository = leaveRepository;
    }

    public Page<LeaveRequest> findAllRequestsPageable(Pageable pageable) {
        return leaveRequestRepository.findAll(pageable);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void makeLeaveRequest(long userId, long leaveId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("Invalid user id: " + userId));
        Leave leave = leaveRepository.findById(leaveId).orElseThrow(() ->
                new IllegalArgumentException("Invalid leave id: " + leaveId));

        long currentLeaveRequestsCount = leaveRequestRepository
                .findByLeaveIdAndUserId(leaveId, userId)
                .stream()
                .filter(leaveRequest ->
                                leaveRequest.getStatus().equals(LeaveRequestStatus.PENDING))
                .count();
        if (currentLeaveRequestsCount > 0) {
            log.info("User already send leave request");
            return;
        }

        switch (leave.getLeaveStatus()) {

            case ACTIVE: {
                if (leave.getUsers().contains(user)) {
                    log.info("User already in leave");
                    break;
                }
                LeaveRequest leaveRequest = LeaveRequest.builder()
                        .user(user)
                        .leave(leave)
                        .status(LeaveRequestStatus.APPROVED)
                        .requestDate(LocalDate.now())
                        .build();
                leaveRequestRepository.save(leaveRequest);
                break;
            }
            case INACTIVE: {
                LeaveRequest leaveRequest = LeaveRequest.builder()
                        .user(user)
                        .leave(leave)
                        .status(LeaveRequestStatus.PENDING)
                        .requestDate(LocalDate.now())
                        .build();
                leaveRequestRepository.save(leaveRequest);
                break;
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void approveLeaveRequest(long leaveRequestId) {
        LeaveRequest leaveRequest = findLeaveRequestById(leaveRequestId);

        Leave leave = leaveRequest.getLeave();
        User user = leaveRequest.getUser();

        switch (leave.getLeaveStatus()) {
            case INACTIVE: {
                leave.setStartTime(LocalDate.now());
                leave.setLeaveStatus(LeaveStatus.ACTIVE);
                user.getLeaves().add(leave);
                leaveRequest.setStatus(LeaveRequestStatus.APPROVED);

                leaveRepository.save(leave);
                log.info("Leave request approved");
                break;
            }
            case ACTIVE: {
                user.getLeaves().add(leave);
                leaveRequest.setStatus(LeaveRequestStatus.APPROVED);

                leaveRepository.save(leave);
                log.info("Leave request approved");
                break;
            }
        }
        leaveRequestRepository.save(leaveRequest);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void rejectLeaveRequest(long leaveRequestId) {
        LeaveRequest leaveRequest = findLeaveRequestById(leaveRequestId);
        leaveRequest.setStatus(LeaveRequestStatus.REJECTED);
        leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest findLeaveRequestById(long leaveRequestId) {
        return leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                new IllegalArgumentException("Invalid leave request id: " + leaveRequestId));
    }
}
