package com.rey.template.viewmodel.pages;

import com.rey.template.viewmodel.util.AuthorizedVM;
import org.zkoss.bind.annotation.Init;

public class HomeVM extends AuthorizedVM {

    @Init
    public void init() {

        validatePage("/pages/home.zul");

    }
}
