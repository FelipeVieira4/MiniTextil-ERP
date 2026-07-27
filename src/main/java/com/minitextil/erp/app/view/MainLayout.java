package com.minitextil.erp.app.view;

import java.util.LinkedHashMap;
import java.util.Map;

import com.minitextil.erp.components.core.InstanceProgram;
import com.minitextil.erp.components.core.ProgramId;
import com.minitextil.erp.components.core.ProgramParams;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@Route("")
@PermitAll
public class MainLayout extends AppLayout {

    private static final long serialVersionUID = 1L;

    private final Tabs instanceTabs = new Tabs();
    private final Div instanceContentArea = new Div();
    private final Map<Tab, InstanceProgram> instances = new LinkedHashMap<>();

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addHeaderContent();
        addDrawerContent();

        instanceContentArea.setSizeFull();
        setContent(instanceContentArea);

        instanceTabs.addSelectedChangeListener(_-> {
            InstanceProgram ip = instances.get(instanceTabs.getSelectedTab());
            if (ip != null) {
                showInstance(ip);
            }
        });
        
        // Abre uma tela inicial padrão ao carregar o ERP
        openNewInstance(ProgramId.HOME, ProgramParams.empty());
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        HorizontalLayout headerLayout = new HorizontalLayout(toggle, instanceTabs);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setFlexGrow(1, instanceTabs);
        headerLayout.setPadding(true);

        addToNavbar(true, headerLayout);
    }

    private void addDrawerContent() {
        Span appName = new Span("MiniTextil ERP");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.MEDIUM);
        
        // Adiciona o título e logo em seguida o menu de navegação no Drawer
        addToDrawer(appName, createNavigation());
    }
    
    private SideNav createNavigation() {
        SideNav navSideBar = new SideNav();
        navSideBar.setWidthFull();

        SideNavItem inicioItem = new SideNavItem("Início");
	        inicioItem.getElement().addEventListener("click", _-> {
	            openNewInstance(ProgramId.HOME, ProgramParams.empty());
	        }
	    );
        navSideBar.addItem(inicioItem);

        SideNavItem operadorModulo = new SideNavItem("Operador");

        SideNavItem cadastroOperadorItem = new SideNavItem("Cadastro Operador");
        cadastroOperadorItem.getElement().addEventListener("click", _-> {
            openNewInstance(ProgramId.CADASTRO_OPERADOR, ProgramParams.empty());
        });

        SideNavItem consultaOperadorItem = new SideNavItem("Consulta Operadores");
        consultaOperadorItem.getElement().addEventListener("click", _-> {
        	openNewInstance(ProgramId.CONSULTA_OPERADOR, ProgramParams.empty());
        });

        operadorModulo.addItem(cadastroOperadorItem, consultaOperadorItem);
        navSideBar.addItem(operadorModulo);

        return navSideBar;
    }
    
    public void openNewInstance(ProgramId programId, ProgramParams params) {
        Span titulo = new Span(programId.getTitulo());
        
        Button btnFechar = new Button(VaadinIcon.CLOSE_SMALL.create());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        btnFechar.getStyle().set("font-size", "12px").set("margin-left", "8px");
        
        HorizontalLayout tabContent = new HorizontalLayout(titulo, btnFechar);
        tabContent.setAlignItems(FlexComponent.Alignment.CENTER);
        tabContent.setSpacing(false);
        
        Tab tab = new Tab(tabContent);
        
        // Gambi para obrigar front-side fechar atela
        btnFechar.getElement().executeJs("$0.addEventListener('click', e => e.stopPropagation());");
        btnFechar.addClickListener(_-> {
            closeInstance(tab);
        });
        
        InstanceProgram instanceProgram = new InstanceProgram(programId, params, tab);

        instances.put(tab, instanceProgram);
        instanceTabs.add(tab);
        instanceTabs.setSelectedTab(tab);
        showInstance(instanceProgram);
    }

    public void closeInstance(Tab tab) {
        InstanceProgram removed = instances.remove(tab);
        if (removed == null) {
            return;
        }
        int index = instanceTabs.indexOf(tab);
        instanceTabs.remove(tab);
        if (!instances.isEmpty()) {
            instanceTabs.setSelectedIndex(Math.max(0, index - 1));
        } else {
            instanceContentArea.removeAll();
        }
    }

    private void showInstance(InstanceProgram instanceProgram) {
        instanceContentArea.removeAll();
        instanceContentArea.add(instanceProgram);
    }
}