package com.minitextil.erp.operador.view;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route("/operador")
@PageTitle("Cadastro Operador")
@PermitAll
public class CadastroOperador extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    public CadastroOperador() {
    	this.setWidthFull();
    	
    	TextField nome = new TextField("Nome");
    	nome.setRequiredIndicatorVisible(true);
    	nome.setRequired(true);
    	
    	EmailField email = new EmailField("Email");
    	
    	PasswordField senha = new PasswordField("Senha");
    	senha.setRequiredIndicatorVisible(true);
    	senha.setRequired(true);
    	
    	PasswordField confirmarSenha = new PasswordField("Confirmar Senha");
    	confirmarSenha.setRequiredIndicatorVisible(true);
    	confirmarSenha.setRequired(true);

    	Div quebra = new Div();
    	FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();

        formLayout.add(nome);
        formLayout.add(email);
        formLayout.setColspan(email, 2); 
        
        formLayout.add(senha);
        
        formLayout.add(quebra);
        formLayout.setColspan(quebra, 2);
        
        formLayout.add(confirmarSenha);
        
        add(formLayout);
    }    
}