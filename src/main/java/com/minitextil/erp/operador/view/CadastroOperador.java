package com.minitextil.erp.operador.view;

import com.minitextil.erp.operador.model.OperadorModel;
import com.minitextil.erp.operador.repository.OperadorRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@Route("/operador")
@PageTitle("Cadastro Operador")
@PermitAll
public class CadastroOperador extends VerticalLayout {
    private static final long serialVersionUID = 1L;

    private final Binder<OperadorModel> binder = new BeanValidationBinder<>(OperadorModel.class);
    private final OperadorRepository repository;

    private final IntegerField id = new IntegerField("Código (Deixe vazio para novo)");
    private final TextField nome = new TextField("Nome");
    private final EmailField email = new EmailField("Email");
    private final PasswordField senha = new PasswordField("Senha");
    private final PasswordField confirmarSenha = new PasswordField("Confirmar Senha");
    private final Checkbox ativo = new Checkbox("Ativo");

    @Autowired
    public CadastroOperador(OperadorRepository repository) {
        this.repository = repository;
        
        this.setWidthFull();
        this.setAlignItems(Alignment.CENTER); 

        nome.setRequiredIndicatorVisible(true);
        nome.setRequired(true);
        senha.setRequiredIndicatorVisible(true);
        senha.setRequired(true);
        confirmarSenha.setRequiredIndicatorVisible(true);
        confirmarSenha.setRequired(true);

        id.setValueChangeMode(ValueChangeMode.ON_BLUR);
        id.addValueChangeListener(event -> {
            Integer idDigitado = event.getValue();
            if (idDigitado != null) {
                puxarDadosOperador(idDigitado.longValue());
            } else {
                limparFormulario();
            }
        });

        binder.forField(nome).bind(OperadorModel::getNome, OperadorModel::setNome);
        binder.bindInstanceFields(this); 

        Button btSalvar = new Button("Salvar", event -> {
            salvar(senha.getValue(), confirmarSenha.getValue());
        });
        btSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("50vw");
        formLayout.setRowSpacing("25px");
        
        formLayout.add(id);
        formLayout.add(new Div());
        formLayout.add(nome);
        formLayout.add(email);
        formLayout.add(senha);
        formLayout.add(confirmarSenha);
        formLayout.add(ativo);
        formLayout.add(new Div());
        formLayout.add(btSalvar);
        
        add(formLayout);
    }

    private void puxarDadosOperador(Long idBusca) {
        Optional<OperadorModel> operadorOpt = repository.findById(idBusca);

        if (operadorOpt.isPresent()) {
            OperadorModel operador = operadorOpt.get();
            binder.readBean(operador);
            senha.setValue(operador.getSenha());
            confirmarSenha.setValue(operador.getSenha());
            Notification.show("Operador encontrado! Modo de edição ativo.");
        } else {
            limparFormulario();
            Notification.show("ID não encontrado. Um novo registro será criado ao salvar.", 4000, Notification.Position.MIDDLE);
        }
    }

    private void salvar(String senhaDigitada, String confirmarSenhaDigitada) {
        OperadorModel operador = new OperadorModel();

        if (binder.writeBeanIfValid(operador)) {
            
            if (!senhaDigitada.equals(confirmarSenhaDigitada)) {
                Notification.show("As senhas não coincidem!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (id.getValue() != null) {
                operador.setId(id.getValue().longValue());
            }

            try {
                OperadorModel salvo = repository.save(operador);
                
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
        email.clear();
        senha.clear();
        confirmarSenha.clear();
        ativo.setValue(true);
    }
}