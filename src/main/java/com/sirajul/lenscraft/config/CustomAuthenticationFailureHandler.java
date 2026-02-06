package com.sirajul.lenscraft.config;

import com.sirajul.lenscraft.exception.BlockedUserFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        log.info("Authentication failure: {}", exception.getMessage());

        // Check if the root cause is a BlockedUserFoundException
        Throwable rootCause = exception.getCause();

        if (rootCause instanceof BlockedUserFoundException ||
                (exception instanceof InternalAuthenticationServiceException &&
                        exception.getMessage() != null && exception.getMessage().contains("is Blocked"))) {

            log.info("Blocked user attempted login");
            // Redirect to login page with userBlocked parameter
            getRedirectStrategy().sendRedirect(request, response, "/login?userBlocked");
        } else {
            log.info("Failed login attempt: {}", exception.getMessage());
            // Redirect to login page with error parameter for other failures
            getRedirectStrategy().sendRedirect(request, response, "/login?error");
        }
    }
}
