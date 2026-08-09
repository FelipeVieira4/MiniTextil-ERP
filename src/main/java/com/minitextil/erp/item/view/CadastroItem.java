package com.minitextil.erp.item.view;

import java.util.Optional;

import com.minitextil.erp.components.core.ProgramErp;
import com.minitextil.erp.item.model.Item;
import com.minitextil.erp.item.model.UnidadeMedida;
import com.minitextil.erp.item.repository.ItemRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CadastroItem extends ProgramErp {
    private static final long serialVersionUID = 1L;
    
	private final Binder<Item> binder = new BeanValidationBinder<>(Item.class);
    private final ItemRepository repository;

    private final IntegerField id = new IntegerField("Código");
    private final Button btObter = new Button(new Icon(VaadinIcon.SEARCH));
    
    private final TextField descricao = new TextField("Descrição");
    private final Checkbox ativo = new Checkbox("Ativo");
    private final ComboBox<UnidadeMedida> unidade = new ComboBox<>("Unidade");


    public CadastroItem(ItemRepository repository) {
        this.repository = repository;
        
        
        this.setWidthFull();
        this.setAlignItems(Alignment.CENTER); 
        
        
        HorizontalLayout idCampo = new HorizontalLayout(id,btObter);
        idCampo.setDefaultVerticalComponentAlignment(HorizontalLayout.Alignment.BASELINE);
        idCampo.setWidthFull();
        
        
        binder.forField(descricao)
                .asRequired("Informe a descrição do Item")
                .bind(Item::getDescricao, Item::setDescricao);

        binder.forField(ativo)
        		.bind(Item::isAtivo, Item::setAtivo);

        
        unidade.setItems(UnidadeMedida.values());
        unidade.setItemLabelGenerator(unidadeItem -> unidadeItem.name());
        
        binder.forField(unidade)
        		.bind(Item::getUnidade,Item::setUnidade);
        

        id.setValueChangeMode(ValueChangeMode.ON_BLUR);
        id.addValueChangeListener(event -> {
            Integer idDigitado = event.getValue();
            if (idDigitado != null) {
                MostrarDadosItem(idDigitado.longValue());
            } else {
                limparFormulario();
            }
        });


        btObter.addClickListener(_->{
        	LookupItem lookup = new LookupItem();

        	lookup.VerTodosItens(this.repository, itemSelecionado -> {
        	    id.setValue(itemSelecionado.getId().intValue());
        	});
        });

        /*
        HorizontalLayout botoes = new HorizontalLayout();
        
        Button btSalvar = new Button("Salvar", _-> salvar());
        btSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button btExcluir = new Button("Excluir", _-> excluir(id.getValue().longValue()));
        btExcluir.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        botoes.add(btSalvar);
        botoes.add(btExcluir);
        */
        
        Button btSalvar = new Button("Salvar", _-> salvar());
        btSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("50vw");
        formLayout.setRowSpacing("25px");
        
        formLayout.add(idCampo, new Div(), descricao, ativo, unidade, new Div(), btSalvar);
        
        add(formLayout);
    }
    
	private void MostrarDadosItem(Long idBusca) {
        Optional<Item> itemOpt = repository.findById(idBusca);

        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            binder.readBean(item);
            
            Notification.show("Item ("+idBusca+") encontrado! Modo de edição ativo.");
        } else {
            limparFormulario();
            Notification.show("ID não encontrado. Um novo registro será criado ao salvar.", 4000, Notification.Position.MIDDLE);
        }
    }

	private void salvar() {
	    boolean isEdicao = id.getValue() != null;

	    Item item= new Item();
	    if (binder.writeBeanIfValid(item)) {
	        
	        if (isEdicao) {
	        	item.setId(id.getValue().longValue());
	        }
	        
	        try {
	            Item itemSalvo = repository.save(item);
	            
	            Notification.show("Item salvo com sucesso! ID: " + itemSalvo.getId())
	                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	            
	            limparFormulario();
	            id.clear();
	            
	        } catch (Exception e) {
	            Notification.show("Erro ao salvar: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
	        }
	        
	    } else {
	        Notification.show("Preencha os campos obrigatórios corretamente.").addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }
	}
	
	/*
	private void excluir(Long idExcluir) {

        try {
            repository.deleteById(idExcluir);
            
            Notification.show("Item Excluir com sucesso! ID: " + idExcluir)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            limparFormulario();
            id.clear();
            
        } catch (Exception e) {
            Notification.show("Erro ao salvar: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
	}
	*/


    private void limparFormulario() {
        binder.readBean(null);
        descricao.clear();
        ativo.setValue(true);
        unidade.clear();
    }
}