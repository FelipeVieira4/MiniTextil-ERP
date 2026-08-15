package com.minitextil.erp.item.view;

import com.minitextil.erp.components.core.program.ProgramContext;
import com.minitextil.erp.components.core.program.ProgramErp;
import com.minitextil.erp.components.core.program.ProgramParams;
import com.minitextil.erp.item.model.Item;
import com.minitextil.erp.item.model.ItemEstoque;
import com.minitextil.erp.item.model.ItemEstoqueId;
import com.minitextil.erp.item.model.UnidadeMedida;
import com.minitextil.erp.item.repository.ItemEstoqueRepository;
import com.minitextil.erp.item.repository.ItemRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.Optional;

public class ControleEstoqueItem extends ProgramErp {
    private ItemEstoqueId estoqueId;
    private ProgramContext context;

    private Long idItem;

    private final Binder<ItemEstoque> binder = new BeanValidationBinder<>(ItemEstoque.class);

    private final ItemRepository itemRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;

    private final TextField descricao = new TextField("Item");
    private final Checkbox ativo = new Checkbox("Controla Fornecedor");
    private final ComboBox<UnidadeMedida> unidade = new ComboBox<>("Unidade");

    public void InicializarCampos(){
        descricao.setReadOnly(true);    // apenas visualizador da descrição do produto
    }

    public ControleEstoqueItem(ItemRepository itemRepository,ItemEstoqueRepository itemEstoqueRepository) {
        this.itemRepository=itemRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;

        setWidthFull();
        setAlignItems(Alignment.CENTER);

        InicializarCampos();

        //Button btSalvar = new Button("Salvar", _ -> salvar());
        // btSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("50vw");
        formLayout.setRowSpacing("25px");

        formLayout.add(descricao, ativo, unidade, new Div());
        add(formLayout);
    }

    public void MostrarDados(){
        if (idItem==null) return;

        Optional<Item> optItem=itemRepository.findById(idItem);

        if (optItem.isPresent()){
            Item item=optItem.get();

            descricao.setValue(item.getId()+" - "+item.getDescricao());
        }

    }

    @Override
    public Component getView() {
        return this;
    }

    @Override
    public void onOpen(ProgramParams params, ProgramContext context) {
        this.context = context;

        if (params.has("idItem")) {
            this.idItem = params.get("idItem", Long.class);
            MostrarDados();
        }
    }

}