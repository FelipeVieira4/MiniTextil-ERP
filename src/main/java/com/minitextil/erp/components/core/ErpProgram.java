package com.minitextil.erp.components.core;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class ErpProgram extends VerticalLayout implements Program {
    private static final long serialVersionUID = 1L;

    public ErpProgram() {}

    @Override
    public Component getView() {
        return this;
    }
}
