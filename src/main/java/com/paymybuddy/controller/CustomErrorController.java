package com.paymybuddy.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Erreur - Pay My Buddy");
    }


    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status == null) return "views/error";

        int statusCode = Integer.parseInt(status.toString());

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "views/404";
        }

        return "views/error";
    }
}
