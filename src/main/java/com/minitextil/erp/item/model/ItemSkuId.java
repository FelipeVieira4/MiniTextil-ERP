package com.minitextil.erp.item.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record ItemSkuId(
        Long itemId,
        Long varianteId
) implements Serializable {};
