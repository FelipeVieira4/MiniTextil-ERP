package com.minitextil.erp.app.view;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {
    private static final long serialVersionUID = 1L;

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        LoginForm login = new LoginForm();
        login.setAction("login");

        add(login);
    }
}
