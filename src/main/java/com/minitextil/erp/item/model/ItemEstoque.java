package com.minitextil.erp.item.model;

import com.minitextil.erp.empresa.model.Empresa;
import jakarta.persistence.*;

@Entity
@Table(name = "item_estoque")
public class ItemEstoque {

    @EmbeddedId
    private ItemEstoqueId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("empresaId")
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    private boolean controlaFornecedor;
    private boolean controlaLote;

    public ItemEstoqueId getId() {
        return id;
    }

    public void setId(ItemEstoqueId id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isControlaFornecedor() {
        return controlaFornecedor;
    }

    public void setControlaFornecedor(boolean controlaFornecedor) {
        this.controlaFornecedor = controlaFornecedor;
    }

    public boolean isControlaLote() {
        return controlaLote;
    }

    public void setControlaLote(boolean controlaLote) {
        this.controlaLote = controlaLote;
    }
}