package com.paymybuddy.util;

import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SessionNotFoundException;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class SessionUtil {

    public static UserSessionDTO getUserSession(HttpSession session) {
        return Optional.ofNullable((UserSessionDTO) session.getAttribute("user"))
                .orElseThrow(() -> new SessionNotFoundException("Aucune session utilisateur trouvée"));
    }
}
