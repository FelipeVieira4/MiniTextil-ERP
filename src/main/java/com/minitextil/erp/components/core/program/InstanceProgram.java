package com.minitextil.erp.components.core.program;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class InstanceProgram extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private final Tabs stackTabs = new Tabs();
    private final Div contentArea = new Div();
    private final Deque<StackEntry> stack = new ArrayDeque<>();
    private final Tab instanceTab; // referência ao Tab desta instância na TAB1

    public InstanceProgram(ProgramId rootProgramId, ProgramParams rootParams, Tab instanceTab) {
        this.instanceTab = instanceTab;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        stackTabs.setWidthFull();
        contentArea.setSizeFull();
        add(stackTabs, contentArea);
        setFlexGrow(1, contentArea);

        pushProgram(rootProgramId, rootParams, null);
    }

    
    public void pushProgram(ProgramId programId, ProgramParams params, Consumer<Object> onResult) {
        if (!stack.isEmpty()) {
            stack.peek().tab().setEnabled(false); // desabilita a TAB do programa anterior
        }

        Tab tab;
        if (!isRoot()) {	// não considerar a bagaça para a o programa "pai" da instância
	        Span titulo = new Span(programId.getTitulo());
	        
	        Button btnFechar = new Button(VaadinIcon.CLOSE_SMALL.create());
	        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
	        btnFechar.getStyle().set("font-size", "12px").set("margin-left", "8px");
	        
	        HorizontalLayout tabContent = new HorizontalLayout(titulo, btnFechar);
	        tabContent.setAlignItems(FlexComponent.Alignment.CENTER);
	        tabContent.setSpacing(false);
	        
	        tab = new Tab(tabContent);
	        
	        	btnFechar.getElement().executeJs("$0.addEventListener('click', e => e.stopPropagation());");
	        btnFechar.addClickListener(_-> {
	        	popProgram();
	        });
        }else {
        	tab = new Tab(programId.getTitulo());
        }
        
        Program program = programId.createInstance();
        StackEntry entry = new StackEntry(programId, tab, program, onResult);
        stack.push(entry);

        stackTabs.add(tab);
        stackTabs.setSelectedTab(tab);

        program.onOpen(params, new DefaultProgramContext(this));
        showContent(program.getView());
        syncInstanceTabLabel();
    }

    public void popProgram() {
        if (isRoot()) {
            return;
        }

        StackEntry closed = stack.pop();
        StackEntry previous = stack.peek();
        
        previous.tab().setEnabled(true);
        stackTabs.setSelectedTab(previous.tab());
        
        stackTabs.remove(closed.tab());
        closed.program().onClose();

        showContent(previous.program().getView());
        syncInstanceTabLabel();
    }
    
    
    public void popProgram(Object result) {
        if (isRoot()) {
            return;
        }

        StackEntry closed = stack.pop();
        StackEntry previous = stack.peek();
        
        previous.tab().setEnabled(true);
        stackTabs.setSelectedTab(previous.tab());
        
        stackTabs.remove(closed.tab());
        closed.program().onClose();

        showContent(previous.program().getView());
        syncInstanceTabLabel();

        if (closed.onResult() != null) {
            closed.onResult().accept(result);
        }
    }

    private void showContent(Component view) {
        contentArea.removeAll();
        contentArea.add(view);
    }

    private void syncInstanceTabLabel() {
        instanceTab.getChildren()
            .filter(HorizontalLayout.class::isInstance)
            .map(HorizontalLayout.class::cast)
            .flatMap(hl -> hl.getChildren().filter(Span.class::isInstance).map(Span.class::cast))
            .findFirst()
            .ifPresent(span -> span.setText(stack.peek().programId().getTitulo()));
    }

    public Tab getInstanceTab() {
        return instanceTab;
    }

    public boolean isRoot() {
        return stack.size() == 0;
    }

    private record StackEntry(ProgramId programId, Tab tab, Program program, Consumer<Object> onResult) {}

    private static class DefaultProgramContext implements ProgramContext {
        private final InstanceProgram owner;

        DefaultProgramContext(InstanceProgram owner) {
            this.owner = owner;
        }

        @Override
        public void openProgram(ProgramId programId, ProgramParams params) {
            owner.pushProgram(programId, params, null);
        }

        @Override
        public void openProgram(ProgramId programId, ProgramParams params, Consumer<Object> onResult) {
            owner.pushProgram(programId, params, onResult);
        }

        @Override
        public void closeProgram() {
            owner.popProgram(null);
        }

        @Override
        public void closeProgram(Object result) {
            owner.popProgram(result);
        }
    }
}