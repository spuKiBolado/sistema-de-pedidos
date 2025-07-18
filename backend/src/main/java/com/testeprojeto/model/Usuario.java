package com.testeprojeto.model;

import com.testeprojeto.model.enums.PapelUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true)
    public String email;

    public String senha;

    public String papel;

    @OneToOne
    @JoinColumn(name = "fornecedor_id")
    public Fornecedor fornecedor;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    public Cliente cliente;
}