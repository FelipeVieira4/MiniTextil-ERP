package com.minitextil.erp.operador.view;

import com.minitextil.erp.components.core.Program;
import com.minitextil.erp.empresa.model.EmpresaModel;
import com.minitextil.erp.empresa.repository.EmpresaRepository;
import com.minitextil.erp.operador.model.OperadorModel;
import com.minitextil.erp.operador.repository.OperadorRepository;
import com.minitextil.erp.operador.service.OperadorService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.List;
import java.util.Optional;

public class CadastroOperador extends VerticalLayout implements Program {
    private static final long serialVersionUID = 1L;
    
	private final Binder<OperadorModel> binder = new BeanValidationBinder<>(OperadorModel.class);
    private final OperadorRepository repository;
    private final OperadorService service;

    private final EmpresaRepository empresaRepository;
    
    private final IntegerField id = new IntegerField("Código (Deixe vazio para novo)");
    private final TextField nome = new TextField("Nome");
    private final TextField loginName = new TextField("Nome Login");
    private final EmailField email = new EmailField("Email");
    private final ComboBox<EmpresaModel> empresa = new ComboBox<>("Empresa Principal");
    private final PasswordField senha = new PasswordField("Nova Senha");
    private final PasswordField confirmarSenha = new PasswordField("Confirmar Senha");
    private final Checkbox ativo = new Checkbox("Ativo");

    public CadastroOperador(OperadorRepository repository, OperadorService service,EmpresaRepository empresaRepository) {
        this.repository = repository;
        this.service = service;
        
        this.empresaRepository=empresaRepository;
        
        this.setWidthFull();
        this.setAlignItems(Alignment.CENTER); 

        List<EmpresaModel> listaEmpresas = empresaRepository.findAll();
        empresa.setItems(listaEmpresas);
        empresa.setItemLabelGenerator(empresa -> empresa.getId() + " - " + empresa.getDescricao());
        
        binder.forField(nome)
                .asRequired("Informe o nome")
                .bind(OperadorModel::getNome, OperadorModel::setNome);

        binder.forField(loginName)
                .asRequired("Informe o login")
                .bind(OperadorModel::getLoginName, OperadorModel::setLoginName);

        binder.forField(email)
                .bind(OperadorModel::getEmail, OperadorModel::setEmail);

        binder.forField(empresa)
        		.bind(OperadorModel::getEmpresa, OperadorModel::setEmpresa);
        
        binder.forField(ativo)
                .bind(OperadorModel::isAtivo, OperadorModel::setAtivo);

        id.setValueChangeMode(ValueChangeMode.ON_BLUR);
        id.addValueChangeListener(event -> {
            Integer idDigitado = event.getValue();
            if (idDigitado != null) {
                ObterDadosOperador(idDigitado.longValue());
            } else {
                limparFormulario();
            }
        });

        Button btSalvar = new Button("Salvar", _-> salvar(senha.getValue(), confirmarSenha.getValue()));
        btSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("50vw");
        formLayout.setRowSpacing("25px");
        
        formLayout.add(id, new Div(), nome, loginName, email, new Div(), empresa, new Div(), senha, confirmarSenha, ativo, new Div(), btSalvar);
        
        add(formLayout);
    }

	private void ObterDadosOperador(Long idBusca) {
        Optional<OperadorModel> operadorOpt = repository.findById(idBusca);

        if (operadorOpt.isPresent()) {
            OperadorModel operador = operadorOpt.get();
            binder.readBean(operador);
            
            if (operador.getEmpresa() != null) {
                empresa.setValue(operador.getEmpresa());
            } else {
                empresa.clear();
            }
            
            senha.clear();
            confirmarSenha.clear();
            Notification.show("Operador encontrado! Modo de edição ativo.");
        } else {
            limparFormulario();
            Notification.show("ID não encontrado. Um novo registro será criado ao salvar.", 4000, Notification.Position.MIDDLE);
        }
    }

	private void salvar(String senhaDigitada, String confirmarSenhaDigitada) {
	    boolean isEdicao = id.getValue() != null;

	    if (!isEdicao && (senhaDigitada.isBlank() || confirmarSenhaDigitada.isBlank())) {
	        Notification.show("Informe e confirme a senha!").addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (!senhaDigitada.isBlank() && !senhaDigitada.equals(confirmarSenhaDigitada)) {
	        Notification.show("As senhas não coincidem!").addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    OperadorModel operador = new OperadorModel();
	    
	    if (binder.writeBeanIfValid(operador)) {
	        
	        if (isEdicao) {
	            operador.setId(id.getValue().longValue());
	        }

	        if (!senhaDigitada.isBlank()) {
	            operador.setSenha(senhaDigitada);
	        }

	        Optional<OperadorModel> operadorExistente = repository.findByLoginName(loginName.getValue());
	        if (operadorExistente.isPresent()) {
	            Long idAtual = id.getValue() != null ? id.getValue().longValue() : null;
	            if (!operadorExistente.get().getId().equals(idAtual)) {
	                Notification.show("Nome de Login já usado por outro operador!!")
	                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
	                return;
	            }
	        }
	        
	        if (operador.getEmpresa() == null) {
	            Notification.show("Selecione uma empresa principal!")
	                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
	            return;
	        }
	       
	        try {
	            OperadorModel salvo = service.salvarOperador(operador);
	            
	            Notification.show("Operador salvo com sucesso! ID: " + salvo.getId())
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
        nome.clear();
        loginName.clear();
        email.clear();
        empresa.clear();
        senha.clear();
        confirmarSenha.clear();
        ativo.setValue(true);
    }

    @Override
    public Component getView() {
        return this;
    }
}