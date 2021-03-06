package com.finalproject.controller;

import com.finalproject.model.entity.Shift;
import com.finalproject.model.entity.User;
import com.finalproject.model.service.ShiftService;
import com.finalproject.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Controller
@RequestMapping("/check_in_out")
public class CheckInOutController {

    private final ShiftService shiftService;
    private final UserService userService;

    @Autowired
    public CheckInOutController(ShiftService shiftService, UserService userService) {
        this.shiftService = shiftService;
        this.userService = userService;
    }

    @GetMapping
    public String getCheckInPage(Model model){
        User employee = userService.getCurrentUser();
        Shift shift = shiftService.findCurrentShift(employee);

        model.addAttribute("shift", shift);

        return "check_in_out";
    }

    @PostMapping(path = "/get_time")
    public String getTotalWorkedTime(Model model) {
        User employee = userService.getCurrentUser();
        Shift shift = shiftService.findCurrentShift(employee);

        shiftService.getTotalWorkedTime();

        model.addAttribute("shift", shift);

        return "check_in_out";
    }

    //PostMappings
    @PostMapping(path = "/check_in")
    public String startWorking(Model model) {
        User employee = userService.getCurrentUser();
        Shift shift = shiftService.findCurrentShift(employee);

        model.addAttribute("shift", shift);
        shiftService.start();
        return "check_in_out";
    }

    @PostMapping(path = "/check_out")
    public String stopWorking(Model model) {
        User employee = userService.getCurrentUser();
        Shift shift = shiftService.findCurrentShift(employee);

        model.addAttribute("shift", shift);
        shiftService.stop();
        return "check_in_out";
    }

    private ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addDialect(new Java8TimeDialect());
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}
