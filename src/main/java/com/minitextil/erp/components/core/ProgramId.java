package com.minitextil.erp.components.core;


import com.minitextil.erp.app.view.HomeView;
import com.minitextil.erp.operador.view.CadastroOperador;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;

// Isso está horroso alterar no futuro

public enum ProgramId {
	HOME("Home", HomeView.class),
    CADASTRO_OPERADOR("Cadastro de Operador", CadastroOperador.class);

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