package com.testeprojeto.model;

import com.testeprojeto.model.Fornecedor;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Produto { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String nome;
    public String descricao;
    public BigDecimal preco;

    @ManyToOne
    public Fornecedor fornecedor;
}