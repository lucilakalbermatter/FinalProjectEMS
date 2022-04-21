package com.finalproject.controller;

import com.finalproject.dto.ShiftDTO;
import com.finalproject.model.entity.User;
import com.finalproject.model.service.UserService;
import com.finalproject.model.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


//@RestController
@Controller
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftService shiftService;
    private final UserService userService;

    @Autowired
    public ShiftController(ShiftService shiftService, UserService userService) {
        this.shiftService = shiftService;
        this.userService = userService;
    }

    //GetMapping

    @GetMapping(path="/assign_shifts")
    public String getAssignShiftsPage(@ModelAttribute("shift") ShiftDTO shiftDTO,
                                      Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userList", userService.getAllEmployees());
        return "assign-shifts";
    }

    @PostMapping(path="/assign_shifts")
    public String registerNewShift(@ModelAttribute("shift") @Valid ShiftDTO shiftDTO,
                                   BindingResult bindingResult,
                                   Model model){
        if (bindingResult.hasErrors()) {
            return "assign-shifts";
        }

        User user = userService.findByFullName(shiftDTO.getAssignedEmployeeName());
        model.addAttribute("user", user);

        shiftService.createNewShift(shiftDTO);

        return "redirect:/profiles/"+user.getId();
    }
}

