package com.minitextil.erp.app.view;

import com.minitextil.erp.app.utils.ApplicationInfo;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("")
@PageTitle("Página Inicial")
@PermitAll
public class HomeView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private final ApplicationInfo appInfo;
    
    public HomeView(ApplicationInfo appInfo) {
    	this.appInfo=appInfo;
    	
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        add(new H1("Bem Vindo"));
        add(new H2("MiniTêxtil ERP versão:"+appInfo.getVersion()));
    }
}