package com.minitextil.erp.app.view;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.minitextil.erp.components.core.program.InstanceProgram;
import com.minitextil.erp.components.core.program.ProgramParams;
import com.minitextil.erp.components.core.program.*;
import com.minitextil.erp.empresa.model.Empresa;
import com.minitextil.erp.empresa.repository.EmpresaRepository;
import com.minitextil.erp.operador.model.UsuarioSessao;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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

    private final EmpresaRepository empresaRepository;
    private final UsuarioSessao usuarioSessao;
    private final ProgramRegistry programRegistry;

    public MainLayout(EmpresaRepository empresaRepository, UsuarioSessao usuarioSessao, ProgramRegistry programRegistry) {
        this.empresaRepository = empresaRepository;
        this.usuarioSessao = usuarioSessao;
        this.programRegistry = programRegistry;

        setPrimarySection(Section.DRAWER);
        addHeaderContent();
        addDrawerContent();

        instanceContentArea.setSizeFull();
        setContent(instanceContentArea);

        instanceTabs.addSelectedChangeListener(_ -> {
            Tab selectedTab = instanceTabs.getSelectedTab();
            if (selectedTab != null) {
                InstanceProgram ip = instances.get(selectedTab);
                if (ip != null) {
                    showInstance(ip);
                }
            }
        });

        // Abre a tela inicial padrão ao carregar o ERP
        openNewInstance("HOME", ProgramParams.empty());
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        HorizontalLayout headerLayout = new HorizontalLayout(toggle, instanceTabs);
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setFlexGrow(1, instanceTabs);
        headerLayout.setPadding(true);

        List<Empresa> listaEmpresas = empresaRepository.findAll();

        ComboBox<Empresa> comboEmpresa = new ComboBox<>("Empresa");
        comboEmpresa.setItems(listaEmpresas);
        comboEmpresa.setItemLabelGenerator(empresa -> empresa.getId() + " - " + empresa.getDescricao());

        if (usuarioSessao.getEmpresaAtiva() != null) {
            comboEmpresa.setValue(usuarioSessao.getEmpresaAtiva());
        }

        comboEmpresa.addValueChangeListener(_ -> {
            if (comboEmpresa.getValue() != null) {
                usuarioSessao.setEmpresaAtiva(comboEmpresa.getValue());
                Notification.show("Empresa (" + usuarioSessao.getEmpresaAtiva().getId() + ") selecionada!");
            }
        });

        headerLayout.add(comboEmpresa);
        addToNavbar(true, headerLayout);
    }

    private void addDrawerContent() {
        H3 appName = new H3("MiniTextil ERP");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.MEDIUM);
        appName.setWidthFull();
        appName.getStyle().set("text-align", "center");

        addToDrawer(appName, createNavigation());
    }

    private SideNav createNavigation() {
        SideNav navSideBar = new SideNav();
        navSideBar.setWidthFull();

        for (ModuloDef modulo : programRegistry.getModulos()) {
            List<ProgramaDef> visiveis = modulo.programas().stream()
                    .filter(ProgramaDef::isVisible)
                    .toList();

            if (visiveis.isEmpty()) {
                continue;
            }

            if ("Geral".equals(modulo.modulo())) {
                visiveis.forEach(p -> navSideBar.addItem(criarItem(p)));
            } else {
                SideNavItem moduloItem = new SideNavItem(modulo.modulo());
                visiveis.forEach(p -> moduloItem.addItem(criarItem(p)));
                navSideBar.addItem(moduloItem);
            }
        }

        return navSideBar;
    }

    private SideNavItem criarItem(ProgramaDef programa) {
        SideNavItem item = new SideNavItem(programa.titulo());
        item.getElement().addEventListener("click", _ ->
                openNewInstance(programa.id(), ProgramParams.empty())
        );
        return item;
    }

    public void openNewInstance(String programaId, ProgramParams params) {
        ProgramaDef programaDef;
        try {
            programaDef = programRegistry.get(programaId);
        } catch (IllegalArgumentException e) {
            Notification.show("Programa não encontrado: " + programaId);
            return;
        }

        Span titulo = new Span(programaDef.titulo());

        Button btnFechar = new Button(VaadinIcon.CLOSE_SMALL.create());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        btnFechar.getStyle().set("font-size", "12px").set("margin-left", "8px");

        HorizontalLayout tabContent = new HorizontalLayout(titulo, btnFechar);
        tabContent.setAlignItems(FlexComponent.Alignment.CENTER);
        tabContent.setSpacing(false);

        Tab tab = new Tab(tabContent);

        btnFechar.getElement().addEventListener("click", _ -> closeInstance(tab))
                .addEventData("event.stopPropagation()");

        // Instancia a abas empilhadas usando a nova assinatura do construtor
        InstanceProgram instanceProgram = new InstanceProgram(programRegistry, programaId, params, tab);

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
            int nextIndex = Math.max(0, index - 1);
            Tab nextTab = (Tab) instanceTabs.getComponentAt(nextIndex);
            instanceTabs.setSelectedTab(nextTab);
        } else {
            instanceContentArea.removeAll();
        }
    }

    private void showInstance(InstanceProgram instanceProgram) {
        instanceContentArea.removeAll();
        instanceContentArea.add(instanceProgram);
    }
}