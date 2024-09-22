package com.paymybuddy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Custom error controller to handle application errors.
 */
@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    /**
     * Handles requests to the /error endpoint.
     * Logs the error and returns the error view.
     *
     * @return the view name for the error page
     */
    @RequestMapping("/error")
    public String handleError() {
        logger.error("Une erreur est survenue.");
        return "views/error";
    }
}
