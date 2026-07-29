package com.minitextil.erp.components.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.minitextil.erp.operador.model.OperadorModel;

@Component
public class SecurityUtils {

    public static OperadorModel getOperadorLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.getPrincipal() instanceof OperadorModel) {
            return (OperadorModel) auth.getPrincipal();
        }
        
        return null;
    }
}