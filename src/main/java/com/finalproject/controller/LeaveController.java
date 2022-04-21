package com.finalproject.controller;

import com.finalproject.dto.LeaveDTO;
import com.finalproject.model.entity.User;
import com.finalproject.model.service.LeaveService;
import com.finalproject.model.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Log4j2
@Controller
@RequestMapping("/leaves")
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;

    @Autowired
    public LeaveController(LeaveService leaveService, UserService userService) {
        this.leaveService = leaveService;
        this.userService = userService;
    }

    //GetMapping
    @GetMapping(path = "/leave_request")
    public String getRequestLeavePage(@ModelAttribute("leave") LeaveDTO shiftDTO,
                                      Model model) {
        model.addAttribute("user", new User());
        return "leave-request";
    }

    @PostMapping(path = "/leave_request")
    public String registerNewShift(@ModelAttribute("leave") @Valid LeaveDTO leaveDTO,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "leave-request";
        }

        model.addAttribute("user", userService.getCurrentUser());

        leaveService.createNewLeave(leaveDTO);

        return "redirect:/profile";
    }
}