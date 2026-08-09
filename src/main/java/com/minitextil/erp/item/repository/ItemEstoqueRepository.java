package com.minitextil.erp.item.repository;

import com.minitextil.erp.item.model.ItemEstoque;
import com.minitextil.erp.item.model.ItemEstoqueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, ItemEstoqueId> {
}
