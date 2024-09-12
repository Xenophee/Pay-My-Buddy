package com.paymybuddy.exception;

import com.paymybuddy.controller.RelationshipsController;
import com.paymybuddy.controller.TransactionsController;
import com.paymybuddy.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final RelationshipsController relationshipsController;
    private final TransactionsController transactionsController;

    @Autowired
    public GlobalExceptionHandler(RelationshipsController relationshipsController, TransactionsController transactionsController) {
        this.relationshipsController = relationshipsController;
        this.transactionsController = transactionsController;
    }

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
    public String handleTransactionsException(TransactionsException ex, Model model, HttpSession session, WebRequest request) {
        model.addAttribute("errorMessage", ex.getMessage());
        transactionsController.populateModel(session, model, 0);
        model.addAttribute("Transaction", new Transaction());
        return "views/transactions";
    }

    @ExceptionHandler(RelationshipsException.class)
    public String handleRelationshipsException(RelationshipsException ex, Model model, HttpSession session) {
        model.addAttribute("errorMessage", ex.getMessage());
        relationshipsController.populateModel(model, session);
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
