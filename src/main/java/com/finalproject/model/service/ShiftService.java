package com.finalproject.model.service;

import com.finalproject.dto.ShiftDTO;
import com.finalproject.model.entity.User;
import com.finalproject.model.entity.Shift;
import com.finalproject.model.repository.AbsenceRepository;
import com.finalproject.model.repository.ShiftRepository;
import com.finalproject.model.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;


@Service
@Log4j2
public class ShiftService {

    private final UserService userService;
    private final ShiftRepository shiftRepository;
    private final AbsenceRepository absenceRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShiftService(UserService userService, ShiftRepository shiftRepository, AbsenceRepository absenceRepository, UserRepository userRepository) {
        this.userService = userService;
        this.shiftRepository = shiftRepository;
        this.absenceRepository = absenceRepository;
        this.userRepository = userRepository;
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
                saveShiftAndUpdateEmployee(employee, shift);
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
                saveShiftAndUpdateEmployee(employee, shift);
            }else if(!shift.getShiftDay().equals(shift.getAssignedDay())){
                shift.setStatusMessage("Sorry, you are trying to access a shift of another day. Please contact your supervisor if you have questions.");
            }else {
                shift.setStatusMessage("Sorry, you need to check in first!");
            }
        }
    }

    public void getTotalWorkedTime() {
        User employee = userService.getCurrentUser();
        Shift shift = findCurrentShift(employee);

        if(shift !=null){
            if(shift.getTempEndTime() != null && shift.getShiftDay().equals(shift.getAssignedDay())){
                Duration difference = Duration.between(shift.getTempStartTime(), shift.getTempEndTime());
                if(difference != null){
                    Duration total = getTotalTimeWorked(shift);
                    this.saveShiftAndUpdateEmployee(employee, shift);

                    shift.setStatusMessage("Time worked today: " + total.toHours() + " hours, "
                            + total.toMinutes() + " minutes, " + total.toSeconds() + " seconds");
                }
            }else if(!shift.getShiftDay().equals(shift.getAssignedDay())){
                shift.setStatusMessage("Sorry, you are trying to access a shift of another day. Please contact your supervisor if you have questions.");
            }else{
                shift.setStatusMessage("Sorry, you need to check in and check out first!");
            }
        }
    }

    public void saveShiftAndUpdateEmployee(User employee, Shift shift){
        if(employee.getShifts().isEmpty()){
            employee.getShifts().add(shift);
            shiftRepository.save(shift);
            userService.save(employee);
        }
        for(Shift temp : employee.getShifts()){
            if(temp.equals(shift)){
                shiftRepository.save(shift);
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

        return total;
    }

    public void getTimeBetweenStartAndEnd(Shift shift){
        Duration total = Duration.between(shift.getTempStartTime(), shift.getTempEndTime());

        shift.setTimeWorkedInLastPeriod(total);
        shift.getWorkedPeriods().add(total);
    }

    public void createNewShift(ShiftDTO shiftDTO) {
        LocalTime startTime = LocalTime.from(this.stringToLocalDateTime(shiftDTO.getAssignedStartTime()));
        LocalTime endTime = LocalTime.from(this.stringToLocalDateTime(shiftDTO.getAssignedEndTime()));

        LocalDateTime assignedStartTime = LocalDateTime.of(shiftDTO.getAssignedDay(), startTime);
        LocalDateTime assignedEndTime = LocalDateTime.of(shiftDTO.getAssignedDay(), endTime);

        Shift shift = Shift
                .builder()
                .assignedDay(shiftDTO.getAssignedDay())
                .assignedStartTime(assignedStartTime)
                .assignedEndTime(assignedEndTime)
                .employee(userService.findByFullName(shiftDTO.getAssignedEmployeeName()))
                .build();

        try {
            this.saveShiftAndUpdateEmployee(shift.getEmployee(), shift);
            log.info("New shift " + shift);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocalTime stringToLocalDateTime(String string){
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(DateTimeFormatter.ofPattern("HH:mm"))
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        return LocalTime.parse(string, formatter);
    }




}

