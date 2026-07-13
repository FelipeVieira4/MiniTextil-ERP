package com.minitextil.erp.operador.view;

import com.minitextil.erp.operador.model.OperadorModel;
import com.minitextil.erp.operador.repository.OperadorRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Route("/operador/consulta")
@PageTitle("Consulta de Operadores")
@PermitAll
public class ConsultaOperador extends VerticalLayout {
    private static final long serialVersionUID = 1L;

    private final OperadorRepository repository;
    private final Grid<OperadorModel> grid = new Grid<>(OperadorModel.class, false);

    private String condicaoIdEscolhida = "=";
    private Integer valorIdFiltrado = null;
    
    private String condicaoNomeEscolhida = "Contém";
    private String valorNomeFiltrado = "";

    @Autowired
    public ConsultaOperador(OperadorRepository repository) {
        this.repository = repository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        Button btnNovoOperador = new Button("Novo Operador", VaadinIcon.PLUS.create(), event -> abrirDialogCadastro());
        btnNovoOperador.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        configurarGrid();

        add(btnNovoOperador, grid);
        listarOperadores();
    }

    private void configurarGrid() {
        grid.setSizeFull();
        grid.setColumnReorderingAllowed(true);
        
        Grid.Column<OperadorModel> colunaId = grid.addColumn(OperadorModel::getId)
                .setSortable(true).setFlexGrow(0).setWidth("160px");
        colunaId.setHeader(criarHeaderComFiltro("ID", criarDialogFiltroId()));
        colunaId.setResizable(true);
        
        Grid.Column<OperadorModel> colunaNome = grid.addColumn(OperadorModel::getNome)
                .setSortable(true).setFlexGrow(1);
        colunaNome.setHeader(criarHeaderComFiltro("Nome", criarDialogFiltroNome()));
        colunaNome.setResizable(true);
        
        grid.addColumn(OperadorModel::getEmail).setHeader("E-mail").setFlexGrow(1);
        grid.addColumn(op -> op.isAtivo() ? "Sim" : "Não").setHeader("Ativo").setWidth("100px");

        grid.addThemeVariants(com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES);
    }

    private HorizontalLayout criarHeaderComFiltro(String textoColuna, Dialog dialogFiltro) {
        HorizontalLayout layout = new HorizontalLayout(new Span(textoColuna));
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setAlignItems(Alignment.CENTER);

        Button btnFiltro = new Button(VaadinIcon.FILTER.create());
        btnFiltro.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        btnFiltro.addClickListener(e -> dialogFiltro.open());

        layout.add(btnFiltro);
        return layout;
    }

    private Dialog criarDialogFiltroId() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Filtrar ID");
        dialog.setModal(false);
        dialog.setWidth("280px");

        ComboBox<String> comboCondicao = new ComboBox<>("Condição");
        comboCondicao.setItems("=", ">", "<");
        comboCondicao.setValue(condicaoIdEscolhida);
        comboCondicao.setWidthFull();

        IntegerField inputValor = new IntegerField("Valor");
        inputValor.setValue(valorIdFiltrado);
        inputValor.setWidthFull();

        VerticalLayout conteudo = new VerticalLayout(comboCondicao, inputValor);
        conteudo.setPadding(false);
        dialog.add(conteudo);

        Button btnAplicar = new Button("Aplicar", e -> {
            condicaoIdEscolhida = comboCondicao.getValue();
            valorIdFiltrado = inputValor.getValue();
            listarOperadores();
            dialog.close();
        });
        btnAplicar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnLimpar = new Button("Limpar", e -> {
            inputValor.clear();
            condicaoIdEscolhida = "=";
            valorIdFiltrado = null;
            listarOperadores();
            dialog.close();
        });

        dialog.getFooter().add(btnLimpar, btnAplicar);
        return dialog;
    }

    private Dialog criarDialogFiltroNome() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Filtrar Nome");
        dialog.setModal(false);
        dialog.setWidth("280px");

        ComboBox<String> comboCondicao = new ComboBox<>("Lógica");
        comboCondicao.setItems("Contém", "Começa com", "Igual a");
        comboCondicao.setValue(condicaoNomeEscolhida);
        comboCondicao.setWidthFull();

        TextField inputTexto = new TextField("Texto");
        inputTexto.setValue(valorNomeFiltrado);
        inputTexto.setWidthFull();

        VerticalLayout conteudo = new VerticalLayout(comboCondicao, inputTexto);
        conteudo.setPadding(false);
        dialog.add(conteudo);

        Button btnAplicar = new Button("Aplicar", e -> {
            condicaoNomeEscolhida = comboCondicao.getValue();
            valorNomeFiltrado = inputTexto.getValue();
            listarOperadores();
            dialog.close();
        });
        btnAplicar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnLimpar = new Button("Limpar", e -> {
            inputTexto.clear();
            condicaoNomeEscolhida = "Contém";
            valorNomeFiltrado = "";
            listarOperadores();
            dialog.close();
        });

        dialog.getFooter().add(btnLimpar, btnAplicar);
        return dialog;
    }

    private void abrirDialogCadastro() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Cadastro Rápido de Operador");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setWidth("400px");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setPadding(false);
        formLayout.setSpacing(true);

        TextField txtNome = new TextField("Nome do Operador");
        txtNome.setWidthFull();
        
        EmailField txtEmail = new EmailField("E-mail");
        txtEmail.setWidthFull();

        formLayout.add(txtNome, txtEmail);
        dialog.add(formLayout);

        Button btnSalvar = new Button("Salvar", click -> {
            Notification.show("Salvando: " + txtNome.getValue());
            dialog.close();
        });
        btnSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", click -> dialog.close());

        dialog.getFooter().add(btnCancelar, btnSalvar);
        dialog.open();
    }

    private void listarOperadores() {
        List<OperadorModel> todos = repository.findAll();

        List<OperadorModel> filtrados = todos.stream()
                .filter(op -> filtrarPorId(op, condicaoIdEscolhida, valorIdFiltrado))
                .filter(op -> filtrarPorNome(op, condicaoNomeEscolhida, valorNomeFiltrado))
                .toList();

        grid.setItems(filtrados);
    }

    private boolean filtrarPorId(OperadorModel op, String condicao, Integer valorFiltro) {
        if (valorFiltro == null) return true;
        long idOperador = op.getId();
        long valor = valorFiltro.longValue();

        return switch (condicao) {
            case ">" -> idOperador > valor;
            case "<" -> idOperador < valor;
            default -> idOperador == valor;
        };
    }

    private boolean filtrarPorNome(OperadorModel op, String condicao, String valorFiltro) {
        if (valorFiltro == null || valorFiltro.trim().isEmpty()) return true;
        
        String nomeOp = op.getNome().toLowerCase();
        String busca = valorFiltro.toLowerCase();

        return switch (condicao) {
            case "Começa com" -> nomeOp.startsWith(busca);
            case "Igual a" -> nomeOp.equals(busca);
            default -> nomeOp.contains(busca); // "Contém"
        };
    }
}