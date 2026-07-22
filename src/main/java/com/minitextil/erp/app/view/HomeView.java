package com.minitextil.erp.app.view;

import com.minitextil.erp.app.utils.ApplicationInfo;
import com.minitextil.erp.components.core.InstanceProgram;
import com.minitextil.erp.components.core.Program;
import com.minitextil.erp.components.core.ProgramContext;
import com.minitextil.erp.components.core.ProgramId;
import com.minitextil.erp.components.core.ProgramParams;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


public class HomeView extends VerticalLayout implements Program {

    private static final long serialVersionUID = 1L;

    private ProgramParams params;
    private ProgramContext context;
    
    public HomeView(ApplicationInfo appInfo) {
    	
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        add(new H1("Bem Vindo"));
        add(new H2("MiniTêxtil ERP versão:"+appInfo.getVersion()));
        
        Button botaoAbrirCadOperador = new Button("Cadastro Operador");
        botaoAbrirCadOperador.addClickListener(_->{
        	this.context.openProgram(ProgramId.CADASTRO_OPERADOR, ProgramParams.empty());
        });
        
        add(botaoAbrirCadOperador);
    }

    public void onOpen(ProgramParams params, ProgramContext context) {
    	this.params=params;
    	this.context=context;
    }
    
	@Override
	public Component getView() {
		return this;
	}
}