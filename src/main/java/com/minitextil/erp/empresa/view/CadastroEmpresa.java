package com.minitextil.erp.empresa.view;

import java.util.Optional;

import com.minitextil.erp.components.core.Program;
import com.minitextil.erp.empresa.model.EmpresaModel;
import com.minitextil.erp.empresa.repository.EmpresaRepository;
import com.minitextil.erp.empresa.service.EmpresaService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

public class CadastroEmpresa extends VerticalLayout implements Program {
	   private static final long serialVersionUID = 1L;
	    
		private final Binder<EmpresaModel> binder = new BeanValidationBinder<>(EmpresaModel.class);
	    private final EmpresaRepository repository;
		private final EmpresaService service;

	    private final IntegerField id = new IntegerField("Código (Deixe vazio para novo)");
	    private final TextField descricao = new TextField("Descrição");
	    private final TextField cnpjEmpresa = new TextField("CNPJ Empresa");
	    
	    private final Checkbox situacao = new Checkbox("Situacao");

	    public CadastroEmpresa(EmpresaRepository repository,EmpresaService service) {
	        this.repository = repository;
	        this.service = service;
	        
	        this.setWidthFull();
	        this.setAlignItems(Alignment.CENTER); 

	        binder.forField(descricao)
	                .asRequired("Informe a descrição")
	                .bind(EmpresaModel::getDescricao, EmpresaModel::setDescricao);

	        binder.forField(cnpjEmpresa)
			        .withConverter(
			                cnpjComMascara -> cnpjComMascara == null ? "" : cnpjComMascara.replaceAll("\\D", ""),
			                cnpjNumerico -> this.service.formatCnpj(cnpjNumerico))
	                .asRequired("Informe o CNPJ da empresa")
	                .bind(EmpresaModel::getCnpj, EmpresaModel::setCnpj);
	        
	        cnpjEmpresa.setPlaceholder("00.000.000/0000-00");
	        
	        
	        binder.forField(situacao)
	                .bind(EmpresaModel::getSituacao, EmpresaModel::setSituacao);

	        id.setValueChangeMode(ValueChangeMode.ON_BLUR);
	        id.addValueChangeListener(event -> {
	            Integer idDigitado = event.getValue();
	            if (idDigitado != null) {
	                MostrarDadosEmpresa(idDigitado.longValue());
	            } else {
	                limparFormulario();
	            }
	        });

	        Button btSalvar = new Button("Salvar", _-> salvar());
	        btSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	        
	        FormLayout formLayout = new FormLayout();
	        formLayout.setWidth("50vw");
	        formLayout.setRowSpacing("25px");
	        
	        formLayout.add(id, new Div(), descricao, cnpjEmpresa, situacao, new Div(), btSalvar);
	        
	        add(formLayout);
	    }

		private void MostrarDadosEmpresa(Long idBusca) {
	        Optional<EmpresaModel> empresaObtida = repository.findById(idBusca);

	        if (empresaObtida.isPresent()) {
	            EmpresaModel empresa= empresaObtida.get();
	            binder.readBean(empresa);
	            Notification.show("Empresa ("+idBusca+") encontrado! Modo de edição ativo.");
	        } else {
	            limparFormulario();
	            Notification.show("ID não encontrado. Um novo registro será criado ao salvar.", 4000, Notification.Position.MIDDLE);
	        }
	    }

	    private void salvar() {

	        EmpresaModel empresaModel=new EmpresaModel();

	        if (binder.writeBeanIfValid(empresaModel)) {
	            
	            if (id.getValue() != null) {
	            	empresaModel.setId(id.getValue().longValue());
	            }
	            
	            try {
	                EmpresaModel empresaSalvo = repository.save(empresaModel);
	                
	                Notification.show("Empresa salvo com sucesso! ID: " + empresaSalvo.getId())
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

	    private void limparFormulario() {
	        binder.readBean(null);
	        descricao.clear();
	        cnpjEmpresa.clear();
	        situacao.setValue(true);
	    }

	    @Override
	    public Component getView() {
	        return this;
	    }
	}