package com.finalproject.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class ShiftDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate assignedDay;
    private String assignedStartTime;
    private String assignedEndTime;
    private String assignedEmployeeName;

}
