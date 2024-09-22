package com.paymybuddy.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller class for handling home page related requests.
 */
@Controller
@RequestMapping("/")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private static final String HOME_VIEW = "views/home";


    /**
     * Adds common attributes to the model.
     *
     * @param model the model to add attributes to
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("title", "Pay My Buddy - Accueil");
    }


    /**
     * Handles GET requests to the root endpoint.
     *
     * @return the view name
     */
    @GetMapping
    public String home() {
        return HOME_VIEW;
    }
}
