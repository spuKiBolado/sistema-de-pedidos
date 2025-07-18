package com.testeprojeto.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.testeprojeto.model.enums.StatusPedido;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDate dataDoPedido;
    public BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    public StatusPedido status; 

    @ManyToOne
    public Cliente cliente;

    @ManyToMany
    @JoinTable(
        name = "pedido_produto",
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "produto_id")

    )
    public List<Produto> itens;
}
