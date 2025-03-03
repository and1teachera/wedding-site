package com.zlatenov.wedding_backend.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SecurityAuditAspect {

    // Log user role changes
    @Before("execution(* com.zlatenov.wedding_backend.service.*.updateUserRole(..)) && args(userId, newRole, ..)")
    public void logRoleChange(JoinPoint joinPoint, Long userId, String newRole) {
        MDC.put("securityAction", "ROLE_CHANGE");
        MDC.put("targetUserId", userId.toString());
        MDC.put("newRole", newRole);
        
        log.info("Security: User role change initiated - User ID: {}, New Role: {}", 
                userId, newRole);
        
        MDC.remove("securityAction");
        MDC.remove("targetUserId");
        MDC.remove("newRole");
    }
    
    // Log account deletions
    @Before("execution(* com.zlatenov.wedding_backend.service.*.deleteUser(..)) && args(userId, ..)")
    public void logAccountDeletion(JoinPoint joinPoint, Long userId) {
        MDC.put("securityAction", "ACCOUNT_DELETION");
        MDC.put("targetUserId", userId.toString());
        
        log.warn("Security: User account deletion initiated - User ID: {}", userId);
        
        MDC.remove("securityAction");
        MDC.remove("targetUserId");
    }
    
    // Log successful authentication
    @AfterReturning(
        pointcut = "execution(* com.zlatenov.wedding_backend.service.UserServiceImpl.authenticateUserWithNames(..))",
        returning = "user")
    public void logSuccessfulAuthentication(JoinPoint joinPoint, Object user) {
        if (user != null) {
            MDC.put("securityAction", "AUTHENTICATION");
            MDC.put("authResult", "SUCCESS");
            
            log.info("Security: Successful authentication");
            
            MDC.remove("securityAction");
            MDC.remove("authResult");
        }
    }
}