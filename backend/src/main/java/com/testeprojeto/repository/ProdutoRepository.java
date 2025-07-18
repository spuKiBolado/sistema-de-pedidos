package com.testeprojeto.repository;

import com.testeprojeto.model.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {
    
    public List<Produto> findByFornecedorId(Long fornecedorId) {
        return list("fornecedor.id", fornecedorId);
    }

}