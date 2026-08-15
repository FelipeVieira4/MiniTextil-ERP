package com.minitextil.erp.item.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "TBL_SKU")
public class LeituraItemSku {
    @Id
    @NotBlank(message = "Código SKU do item campo obrigatório!")
    private String codItemSku;

    private Item item;
}
