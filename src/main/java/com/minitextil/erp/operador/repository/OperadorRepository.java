package com.minitextil.erp.operador.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minitextil.erp.operador.model.OperadorModel;

@Repository
public interface OperadorRepository extends JpaRepository<OperadorModel, Long>{
	Optional<OperadorModel> findByLoginName(String loginName);
}
