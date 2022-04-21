package com.finalproject.controller;

import com.finalproject.dto.UpdateUserDTO;
import com.finalproject.model.entity.*;
import com.finalproject.model.service.LeaveService;
import com.finalproject.util.exception.UsernameNotUniqueException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@Log4j2
@RequestMapping("/leave")
public class LeaveRequestController {

    private final LeaveService leaveService;

    public LeaveRequestController(LeaveService leaveRequestService) {
        this.leaveService = leaveRequestService;
    }

    @GetMapping("/accept/{id}")
    public String acceptLeave(@PathVariable("id") long id, HttpServletRequest request) {
        Leave leave = leaveService.getLeaveById(id);
        User user = leave.getEmployee();
        leaveService.accept(leave);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/reject/{id}")
    public String rejectLeave(@PathVariable("id") long id, HttpServletRequest request) {
        Leave leave = leaveService.getLeaveById(id);
        User user = leave.getEmployee();
        leaveService.reject(leave);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

}
