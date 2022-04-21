package com.finalproject.model.service;

import com.finalproject.dto.LeaveDTO;
import com.finalproject.model.entity.Leave;
import com.finalproject.model.entity.LeaveRequestStatus;
import com.finalproject.model.entity.User;
import com.finalproject.model.entity.Shift;
import com.finalproject.model.repository.LeaveRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


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

    //Format the date and time
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss a");
    //DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");


    public void start() {
        User employee = userService.getCurrentUser();
        Shift shift = findCurrentShift(employee);

        if(Objects.nonNull(shift)){
            shift.setAbsent(false);
            shift.setShiftDay(LocalDate.now());

            if (!(shift.isCurrentlyWorking()) && shift.getShiftDay().equals(shift.getAssignedDay())) {
                LocalDateTime now = LocalDateTime.now();
                if(shift.getCheckIns().isEmpty()){
                    shift.setFirstCheckIn(now);
                }
                shift.setCurrentlyWorking(true);
                shift.setTempStartTime(now);
                shift.getCheckIns().add(now);
                shift.setStatusMessage("Check in at: " + now.format(formatter));
                //saveShiftAndUpdateEmployee(employee, shift);
            }else if(!(shift.isCurrentlyWorking()) && !shift.getShiftDay().equals(shift.getAssignedDay())){
                shift.setStatusMessage("Sorry, you are trying to access a shift of another day. Please contact your supervisor if you have questions.");
            }
            else {
                shift.setStatusMessage("Sorry, you need to check out first!");
            }
        }
    }

    public void stop() {
        User employee = userService.getCurrentUser();
        Shift shift = findCurrentShift(employee);

        if(shift !=null){
            if (shift.isCurrentlyWorking() && shift.getShiftDay().equals(shift.getAssignedDay())) {
                LocalDateTime now = LocalDateTime.now();
                shift.setCurrentlyWorking(false);
                shift.setTempEndTime(now);
                shift.getCheckOuts().add(now);
                this.getTimeBetweenStartAndEnd(shift);
                shift.setStatusMessage("Check out at: " + now.format(formatter));
                //saveLeaveAndUpdateEmployee(employee, shift);
            }else if(!shift.getShiftDay().equals(shift.getAssignedDay())){
                shift.setStatusMessage("Sorry, you are trying to access a shift of another day. Please contact your supervisor if you have questions.");
            }else {
                shift.setStatusMessage("Sorry, you need to check in first!");
            }
        }
    }

    public void getTotalWorkedTime() {
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

    public Shift findCurrentShift(User employee){
        LocalDate currentDate = LocalDate.now();
        Shift shift = null;
        for(Shift temp : employee.getShifts()){
            if(temp.getAssignedDay().equals(currentDate)){
                shift = temp;
            }
        }
        return shift;
    }

    public Duration getTotalTimeWorked(Shift shift) {
        long mils = 0;
        for (Duration d : shift.getWorkedPeriods()) {
            mils += d.toMillis();
        }
        Duration total = Duration.ofMillis(mils);
        shift.setTotalTimeWorked(total);
        String formattedTotalWorkedTime = total.toHours() + " hrs, "
                + total.toMinutes() + " min, " + total.toSeconds() + " sec";
        shift.setTotalTime(formattedTotalWorkedTime);

        return total;
    }

    public void getTimeBetweenStartAndEnd(Shift shift){
        Duration total = Duration.between(shift.getTempStartTime(), shift.getTempEndTime());

        shift.setTimeWorkedInLastPeriod(total);
        shift.getWorkedPeriods().add(total);
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

