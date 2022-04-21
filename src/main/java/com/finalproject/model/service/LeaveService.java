package com.finalproject.model.service;

import com.finalproject.dto.LeaveDTO;
import com.finalproject.model.entity.*;
import com.finalproject.model.repository.LeaveRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@Log4j2
public class LeaveService {

    private final LeaveRepository leaveRepository;

    public LeaveService(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    public Page<Leave> findAllLeavesPageable(Pageable pageable) {
        return leaveRepository.findAll(pageable);
    }

    public Leave createLeave(LeaveDTO leaveDTO) {
        Leave leave = Leave
                .builder()
                .description(leaveDTO.getDescription())
                .leaveReason(leaveDTO.getLeaveReason())
                .startTime(leaveDTO.getStartTime())
                .endTime(leaveDTO.getEndTime())
                .leaveStatus(LeaveStatus.INACTIVE)
                .build();

            leaveRepository.save(leave);
            log.info("New leave " + leave);

        return leave;
    }

    public Leave findLeaveById(long leaveId) {
        return leaveRepository.findById(leaveId).orElseThrow(() ->
                new IllegalArgumentException("Invalid leave id: " + leaveId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteLeave(long id) {
        Leave leave = findLeaveById(id);
        Set<User> users = leave.getUsers();
        for (User user : users) {
            user.getLeaves().remove(leave);
        }
        leaveRepository.delete(leave);
    }

}

