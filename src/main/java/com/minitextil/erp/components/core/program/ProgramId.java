package com.minitextil.erp.components.core.program;


import com.minitextil.erp.app.view.HomeView;
import com.minitextil.erp.empresa.view.CadastroEmpresa;
import com.minitextil.erp.item.view.CadastroItem;
import com.minitextil.erp.item.view.ConsultaItem;
import com.minitextil.erp.operador.view.CadastroOperador;
import com.minitextil.erp.operador.view.ConsultaOperador;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;

// Isso está horroso alterar no futuro

public enum ProgramId {
	HOME("Home", HomeView.class),
    CADASTRO_OPERADOR("Cadastro de Operador", CadastroOperador.class),
	CONSULTA_OPERADOR("Consulta de Operador", ConsultaOperador.class),
	CADASTRO_EMPRESA("Cadastro de Empresa", CadastroEmpresa.class),
	CADASTRO_ITEM("Cadastro de Item", CadastroItem.class),
	CONSULTA_ITEM("Consulta de Item", ConsultaItem.class);
	
	
    private final String titulo;
    private final Class<? extends Program> viewClass;

    ProgramId(String titulo, Class<? extends Program> viewClass) {
        this.titulo = titulo;
        this.viewClass = viewClass;
    }

    public String getTitulo() {
        return titulo;
    }

    public Program createInstance() {
        return Instantiator.get(UI.getCurrent()).getOrCreate(viewClass);
    }
}