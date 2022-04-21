package com.finalproject.controller;

import com.finalproject.dto.LeaveDTO;
import com.finalproject.model.entity.*;
import com.finalproject.model.service.LeaveService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller that react on leave related requests
 * @see Leave
 */
@Log4j2
@Controller
public class LeaveController {
    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/leaves")
    public String getLeavesPage(Model model,
                                    @PageableDefault(sort = {"id"},
                                            direction = Sort.Direction.DESC,
                                            size = 5) Pageable pageable) {
        model.addAttribute("leavePage", leaveService.findAllLeavesPageable(pageable));
        return "leaves";
    }

    @GetMapping("/leaves/add")
    public String getAddLeavePage(Model model,
                                     @ModelAttribute("leave") LeaveDTO leaveDto) {
        model.addAttribute("reasons", LeaveReason.values());
        return "add-leave";
    }

    @PostMapping("/leaves/add")
    public String addActivity(@ModelAttribute("leave") @Valid LeaveDTO leaveDTO,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reasons", LeaveReason.values());
            return "add-leave";
        }

        leaveService.createLeave(leaveDTO);

        return "redirect:/leaves";
    }

    @GetMapping("/leaves/delete/{id}")
    public String deleteLeave(@PathVariable("id") long leaveId) {
        leaveService.deleteLeave(leaveId);
        return "redirect:/leaves";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return "error/404";
    }
}
