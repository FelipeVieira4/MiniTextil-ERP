package com.minitextil.erp.components.core.program;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class ProgramErp extends VerticalLayout implements Program {
    public ProgramErp() {}

    @Override
    public Component getView() {
        return this;
    }
}
