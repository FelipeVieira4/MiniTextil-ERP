package com.minitextil.erp.operador.view;

import com.minitextil.erp.components.core.Program;
import com.minitextil.erp.components.grid.GridComponent;
import com.minitextil.erp.components.grid.TipoColunas;
import com.minitextil.erp.operador.model.OperadorModel;
import com.minitextil.erp.operador.repository.OperadorRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class ConsultaOperador extends VerticalLayout implements Program {

    private static final long serialVersionUID = 1L;

    private final GridComponent<OperadorModel> grid = new GridComponent<>(OperadorModel.class);
    private final OperadorRepository repository;

    public ConsultaOperador(OperadorRepository operadorRepository) {
        this.repository = operadorRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configurarGrid();
        add(grid);

        carregarOperadores();
    }

    private void configurarGrid() {
        grid.setColumnReorderingAllowed(true);

        grid.addColunaComFiltro(OperadorModel::getId, "ID", TipoColunas.Numerico);
        grid.addColunaComFiltro(OperadorModel::getNome, "Nome", TipoColunas.Alfanumerico);
        grid.addColunaComFiltro(OperadorModel::getEmail, "Email", TipoColunas.Alfanumerico);
    }

    private void carregarOperadores() {
        List<OperadorModel> listaOperadores = repository.findAll();
        grid.setItensComFiltro(listaOperadores);
    }

    @Override
    public Component getView() {
        return this;
    }
}