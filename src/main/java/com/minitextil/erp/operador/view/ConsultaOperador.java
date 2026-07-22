package com.minitextil.erp.operador.view;


import com.minitextil.erp.components.core.Program;
import com.minitextil.erp.components.grid.GridComponent;
import com.minitextil.erp.components.grid.TipoColunas;
import com.minitextil.erp.operador.model.OperadorModel;
import com.minitextil.erp.operador.repository.OperadorRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class ConsultaOperador extends VerticalLayout implements Program{
    private static final long serialVersionUID = 1L;

    private final OperadorRepository repository;
    private final GridComponent<OperadorModel> grid = new GridComponent<>(OperadorModel.class);

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

        configurarGrid();

        add(grid);
        listarOperadores();
    }

    private void configurarGrid() {
        grid.setColumnReorderingAllowed(true);

        grid.addColunaComFiltro(OperadorModel::getId, "ID", TipoColunas.Numerico, dados -> {
            this.condicaoIdEscolhida = (String) dados[0];
            this.valorIdFiltrado = (IntegerField) dados[1] != null ? (Integer) dados[1] : null;
        });

        grid.addColunaComFiltro(OperadorModel::getNome, "Nome", TipoColunas.Alfanumerico, dados -> {
            this.condicaoNomeEscolhida = (String) dados[0];
            this.valorNomeFiltrado = (String) dados[1];
        });

        grid.addColumn(OperadorModel::getEmail).setHeader("E-mail");

        grid.addFiltroChangeListener(_-> listarOperadores());
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

	@Override
	public Component getView() {
		return this;
	}
}