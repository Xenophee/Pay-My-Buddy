package com.paymybuddy.util;

import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SessionNotFoundException;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

/**
 * Utility class for session operations.
 */
public class SessionUtil {

    /**
     * Retrieves the user session from the HTTP session.
     *
     * @param session the HTTP session
     * @return the user session data transfer object
     * @throws SessionNotFoundException if no user session is found
     */
    public static UserSessionDTO getUserSession(HttpSession session) {
        return Optional.ofNullable((UserSessionDTO) session.getAttribute("user"))
                .orElseThrow(() -> new SessionNotFoundException("Aucune session utilisateur trouvée"));
    }
}
