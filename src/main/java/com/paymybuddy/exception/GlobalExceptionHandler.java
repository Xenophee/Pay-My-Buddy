package com.paymybuddy.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SignInException.class)
    public String handleSignInException(SignInException ex, Model model, WebRequest request) {
        String email = request.getParameter("email");
        model.addAttribute("email", email);
        model.addAttribute("errorMessage", ex.getMessage());
        return "views/sign-in";
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public String handleSessionNotFoundException(SessionNotFoundException ex) {
        return "redirect:/sign-in";
    }

    @ExceptionHandler(TransactionsException.class)
    public String handleTransactionsException(TransactionsException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/transactions";
    }

    @ExceptionHandler(RelationshipsException.class)
    public String handleRelationshipsException(RelationshipsException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "views/relationships";
    }

    @ExceptionHandler(UnexpectedNotFoundException.class)
    public String handleUnexpectedNotFoundException(UnexpectedNotFoundException ex) {
        return "views/error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String httpMethod = request.getMethod();
        logger.error("Erreur 404 sur {} avec la méthode {} : ", requestUri, httpMethod, ex);
        return "views/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String httpMethod = request.getMethod();
        logger.error("Erreur sur {} avec la méthode {} : ", requestUri, httpMethod, ex);
        return "views/error";
    }


}
