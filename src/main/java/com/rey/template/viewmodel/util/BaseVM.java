package com.rey.template.viewmodel.util;

import com.rey.template.security.CurrentUserService;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public abstract class BaseVM {

    @WireVariable
    protected CurrentUserService currentUserService;

    protected void validateLogin() {

        if (!currentUserService.isLoggedIn()) {

            Executions.sendRedirect("/login.zul");

        }
    }
}
