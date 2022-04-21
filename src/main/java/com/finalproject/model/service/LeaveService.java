package com.finalproject.model.service;

import com.finalproject.dto.LeaveDTO;
import com.finalproject.model.entity.Leave;
import com.finalproject.model.entity.LeaveRequestStatus;
import com.finalproject.model.entity.User;
import com.finalproject.model.repository.LeaveRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LeaveService {

    private final UserService userService;
    private final LeaveRepository leaveRepository;

    @Autowired
    public LeaveService(UserService userService, LeaveRepository leaveRepository) {
        this.userService = userService;
        this.leaveRepository = leaveRepository;
    }

    public void saveLeaveAndUpdateEmployee(User employee, Leave leave){
        if(employee.getLeaves().isEmpty()){
            employee.getLeaves().add(leave);
            leaveRepository.save(leave);
            userService.save(employee);
        }
        for(Leave temp : employee.getLeaves()){
            if(temp.equals(leave)){
                leaveRepository.save(leave);
                userService.save(employee);
            }
        }
    }

    public void createNewLeave(LeaveDTO leaveDTO) {
        Leave leave = Leave
                .builder()
                .description(leaveDTO.getDescription())
                .leaveReason(leaveDTO.getLeaveReason())
                .startTime(leaveDTO.getStartTime())
                .endTime(leaveDTO.getEndTime())
                .employee(userService.getCurrentUser())
                .supervisor(userService.getCurrentUser().getSupervisorName())
                .status(LeaveRequestStatus.PENDING.name())
                .build();

        try {
            this.saveLeaveAndUpdateEmployee(leave.getEmployee(), leave);
            log.info("New leave request " + leave);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Leave getLeaveById(long id) {
        return leaveRepository.findById(id).orElseThrow();
    }

    public void saveLeaveAndUpdateEmployee(Leave leave){
        leaveRepository.save(leave);
        userService.save(leave.getEmployee());
    }

    public void accept(Leave leave) {
        leave.setStatus(LeaveRequestStatus.APPROVED.name());
        leave.setStatusUpdated(true);
        this.saveLeaveAndUpdateEmployee(leave);
    }

    public void reject(Leave leave) {
        leave.setStatus(LeaveRequestStatus.REJECTED.name());
        leave.setStatusUpdated(true);
        this.saveLeaveAndUpdateEmployee(leave);
    }
}

