package com.rey.template.viewmodel.common;

import com.rey.template.service.AuthorizationService;
import com.rey.template.util.UrlConstant;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.WireVariable;

public abstract class AuthorizedVM extends BaseVM {

    @WireVariable
    private AuthorizationService authorizationService;

    protected void validatePage(
            String page
    ) {

        boolean authorized =
                authorizationService.canAccessPage(
                        currentUserService.getCurrentUsername(),
                        currentUserService
                                .getCurrentResponsibility()
                                .getCode(),
                        page
                );

        if (!authorized) {

            Executions.sendRedirect(
                    UrlConstant.URL_ACCESS_DENIED_ZUL
            );
        }
    }
}
