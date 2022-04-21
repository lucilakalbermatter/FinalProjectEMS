package com.finalproject.controller;

import com.finalproject.model.entity.*;
import com.finalproject.model.service.LeaveRequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Log4j2
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping("/leaves/request")
    public String getLeaveRequests(Model model,
                                      @PageableDefault(sort = {"id"},
                                              direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("leaveRequests", leaveRequestService.findAllRequestsPageable(pageable));
        return "leave-requests";
    }

    @GetMapping("/leaves/request/add/{id}")
    public String makeAddLeaveRequest(@AuthenticationPrincipal User user,
                                         @PathVariable("id") long leaveId) {
        leaveRequestService.makeLeaveRequest(user.getId(), leaveId);
        return "redirect:/leaves";
    }


    @GetMapping("/leaves/request/approve/{id}")
    public String approveLeaveRequest(@PathVariable("id") long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestService
                .findLeaveRequestById(leaveRequestId);

        if (!leaveRequest.getStatus().equals(LeaveRequestStatus.PENDING)) {
            return "redirect:/leaves/request";
        }

        leaveRequestService.approveLeaveRequest(leaveRequestId);


        return "redirect:/leaves/request";
    }

    @GetMapping("/leaves/request/reject/{id}")
    public String rejectLeaveRequest(@PathVariable("id") long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestService.findLeaveRequestById(leaveRequestId);

        if (!leaveRequest.getStatus().equals(LeaveRequestStatus.PENDING)) {
            return "redirect:/leaves/request";
        }

        leaveRequestService.rejectLeaveRequest(leaveRequestId);


        return "redirect:/leaves/request";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return "error/404";
    }
}
