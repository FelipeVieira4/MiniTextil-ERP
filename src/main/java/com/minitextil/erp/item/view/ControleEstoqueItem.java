package com.minitextil.erp.item.view;

import com.minitextil.erp.components.core.ProgramContext;
import com.minitextil.erp.components.core.ProgramErp;
import com.minitextil.erp.components.core.ProgramParams;
import com.minitextil.erp.item.model.ItemEstoque;
import com.minitextil.erp.item.model.ItemEstoqueId;
import com.minitextil.erp.item.model.UnidadeMedida;
import com.minitextil.erp.item.repository.ItemEstoqueRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.Optional;

public class ControleEstoqueItem extends ProgramErp {
    private static final long serialVersionUID = 1L;

    private ItemEstoqueId estoqueId;

	private final Binder<ItemEstoque> binder = new BeanValidationBinder<>(ItemEstoque.class);
    private final ItemEstoqueRepository repository;

    private final TextField descricao = new TextField("Descrição");
    private final Checkbox ativo = new Checkbox("Controla Fornecedor");
    private final ComboBox<UnidadeMedida> unidade = new ComboBox<>("Unidade");


    public ControleEstoqueItem(ItemEstoqueRepository repository) {
        this.repository = repository;
        
        
        this.setWidthFull();
        this.setAlignItems(Alignment.CENTER); 


        
        Button btSalvar = new Button("Salvar", _-> salvar());
        btSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("50vw");
        formLayout.setRowSpacing("25px");
        
        formLayout.add(descricao, ativo, unidade, new Div(), btSalvar);
        
        add(formLayout);
    }
    
	private void MostrarDadosItem(ItemEstoqueId idBusca) {
        Optional<ItemEstoque> itemOpt = repository.findById(idBusca);

        if (itemOpt.isPresent()) {
            ItemEstoque itemEstoque = itemOpt.get();
            binder.readBean(itemEstoque);
            
            Notification.show("Item ("+itemEstoque.getItem().getId()+") encontrado! Modo de edição ativo.");
        } else {
            limparFormulario();
            Notification.show("ID não encontrado. Um novo registro será criado ao salvar.", 4000, Notification.Position.MIDDLE);
        }
    }

	private void salvar() {
	    //boolean isEdicao = id.getValue() != null;

        ItemEstoque itemEstoque= new ItemEstoque();
	    if (binder.writeBeanIfValid(itemEstoque)) {
	        /*
	        if (isEdicao) {
	        	item.setId(id.getValue().longValue());
	        }*/
	        
	        try {
                ItemEstoque itemEstoqueSalvo = repository.save(itemEstoque);
	            
	            Notification.show("Configuração de Estoque Item salvo com sucesso! ID: " + itemEstoqueSalvo.getItem().getId())
	                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	            
	            limparFormulario();
	            //id.clear();
	            
	        } catch (Exception e) {
	            Notification.show("Erro ao salvar: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
	        }
	        
	    } else {
	        Notification.show("Preencha os campos obrigatórios corretamente.").addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }
	}



    private void limparFormulario() {
        binder.readBean(null);
        descricao.clear();
        ativo.setValue(true);
        unidade.clear();
    }

    @Override
    public void onOpen(ProgramParams params, ProgramContext context) {
        super.onOpen(context);

        estoqueId=params.get("estoqueId",ItemEstoqueId);
    }
}