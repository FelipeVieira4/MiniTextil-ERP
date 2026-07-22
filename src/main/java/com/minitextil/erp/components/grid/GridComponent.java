package com.minitextil.erp.components.grid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.ValueProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GridComponent<T> extends Grid<T> {

    private static final long serialVersionUID = 1L;
    
    private final List<Consumer<Void>> listenersDeFiltro = new ArrayList<>();

    public GridComponent(Class<T> beanType) {
        super(beanType, false);
        setSizeFull();
        addThemeVariants(com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES);
    }


    public void addColunaComFiltro(ValueProvider<T, ?> valueProvider, String nomeColuna, TipoColunas tipoColuna, Consumer<Object[]> callbackFiltro) {
        Grid.Column<T> coluna = this.addColumn(valueProvider)
                .setSortable(true)
                .setResizable(true);

        if (tipoColuna == TipoColunas.Numerico) {
            coluna.setFlexGrow(0).setWidth("140px");
        } else {
            coluna.setFlexGrow(1);
        }

        Dialog dialogFiltro = criarDialogFiltroDinamico(nomeColuna, tipoColuna, callbackFiltro);

        coluna.setHeader(criarHeaderComFiltro(nomeColuna, dialogFiltro));
    }

    private HorizontalLayout criarHeaderComFiltro(String textoColuna, Dialog dialogFiltro) {
        HorizontalLayout layout = new HorizontalLayout(new Span(textoColuna));
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setAlignItems(Alignment.CENTER);

        Button btnFiltro = new Button(VaadinIcon.FILTER.create());
        btnFiltro.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        btnFiltro.addClickListener(_-> dialogFiltro.open());

        layout.add(btnFiltro);
        return layout;
    }

    private Dialog criarDialogFiltroDinamico(String nomeColuna, TipoColunas tipo, Consumer<Object[]> callbackFiltro) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Filtrar " + nomeColuna);
        dialog.setWidth("280px");

        VerticalLayout conteudo = new VerticalLayout();
        conteudo.setPadding(false);

        if (tipo == TipoColunas.Numerico) {
            ComboBox<String> comboCondicao = new ComboBox<>("Condição");
            comboCondicao.setItems("=", ">", "<");
            comboCondicao.setValue("=");
            comboCondicao.setWidthFull();

            IntegerField inputValor = new IntegerField("Valor");
            inputValor.setWidthFull();

            conteudo.add(comboCondicao, inputValor);
            dialog.add(conteudo);

            Button btnAplicar = new Button("Aplicar", _-> {
                callbackFiltro.accept(new Object[]{comboCondicao.getValue(), inputValor.getValue()});
                notificarMudancaFiltro();
                dialog.close();
            });
            btnAplicar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button btnLimpar = new Button("Limpar", _-> {
                inputValor.clear();
                callbackFiltro.accept(new Object[]{"=", null});
                notificarMudancaFiltro();
                dialog.close();
            });
            dialog.getFooter().add(btnLimpar, btnAplicar);

        } else if (tipo == TipoColunas.Alfanumerico) {
            ComboBox<String> comboCondicao = new ComboBox<>("Lógica");
            comboCondicao.setItems("Contém", "Começa com", "Igual a");
            comboCondicao.setValue("Contém");
            comboCondicao.setWidthFull();

            TextField inputTexto = new TextField("Texto");
            inputTexto.setWidthFull();

            conteudo.add(comboCondicao, inputTexto);
            dialog.add(conteudo);

            Button btnAplicar = new Button("Aplicar", _-> {
                callbackFiltro.accept(new Object[]{comboCondicao.getValue(), inputTexto.getValue()});
                notificarMudancaFiltro();
                dialog.close();
            });
            btnAplicar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button btnLimpar = new Button("Limpar", _-> {
                inputTexto.clear();
                callbackFiltro.accept(new Object[]{"Contém", ""});
                notificarMudancaFiltro();
                dialog.close();
            });
            dialog.getFooter().add(btnLimpar, btnAplicar);
        }

        return dialog;
    }

    public void addFiltroChangeListener(Consumer<Void> listener) {
        this.listenersDeFiltro.add(listener);
    }

    private void notificarMudancaFiltro() {
        for (Consumer<Void> listener : listenersDeFiltro) {
            listener.accept(null);
        }
    }
}