package com.zlatenov.wedding_backend.aspect;

import com.zlatenov.wedding_backend.exception.InvalidCredentialsException;
import com.zlatenov.wedding_backend.service.LoginAuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Angel Zlatenov
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LoginAuditAspect {
    private final LoginAuditService loginAuditService;

    @AfterReturning(pointcut = "execution(* com.zlatenov.wedding_backend.service.UserService.authenticateUser(..)) && args(firstName, lastName, password, ipAddress, userAgent)", returning = "user")
    public void logSuccessfulLogin(String firstName, String lastName, String password, String ipAddress, String userAgent) {
        loginAuditService.logSuccessfulAttempt(firstName, lastName, ipAddress, userAgent);
    }

    @AfterThrowing(pointcut = "execution(* com.zlatenov.wedding_backend.service.UserService.authenticateUser(..)) && args(firstName, lastName, password, ipAddress, userAgent)", throwing = "exception")
    public void logFailedLogin(String firstName, String lastName, String password, String ipAddress, String userAgent, Exception exception) {
        if (exception instanceof InvalidCredentialsException) {
            loginAuditService.logFailedAttempt(firstName, lastName, ipAddress, userAgent);
        }
    }
}

