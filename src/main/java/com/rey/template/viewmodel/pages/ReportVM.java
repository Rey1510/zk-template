package com.rey.template.viewmodel.pages;

import com.rey.template.viewmodel.util.AuthorizedVM;
import org.zkoss.bind.annotation.Init;

public class ReportVM extends AuthorizedVM {

    @Init
    public void init() {

        validatePage("/pages/report.zul");

    }
}
