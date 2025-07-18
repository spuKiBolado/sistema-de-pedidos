package com.testeprojeto.resource;

import com.testeprojeto.model.Fornecedor;
import com.testeprojeto.repository.FornecedorRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import com.testeprojeto.repository.ProdutoRepository;
import com.testeprojeto.model.Produto;

@Path ("/fornecedores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FornecedorResource {

    @Inject
    FornecedorRepository repository;

    @Inject
    ProdutoRepository produtoRepository;

    @GET
    @Path("/{id}/produtos")
    public List<Produto> getProdutosDoFornecedor(@PathParam("id") Long id) {
        return produtoRepository.findByFornecedorId(id);
    }

    @POST
    @Transactional
    public Response criarFornecedor (Fornecedor fornecedor){
        repository.persist(fornecedor);
        return Response.created(URI.create("/fornecedor" + fornecedor.id)).entity(fornecedor).build();
    }

    @GET
    public List<Fornecedor> listarTodos(){
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    public Fornecedor buscarPorId(@PathParam("id") Long id){
        Fornecedor fornecedor = repository.findById(id);
        if (fornecedor == null){
            throw new NotFoundException();
        }

        return fornecedor;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizarFornecedor(@PathParam("id") Long id, Fornecedor fornecedorAtualizado){
        Fornecedor fornecedorExistente = buscarPorId(id);
        fornecedorExistente.nome = fornecedorAtualizado.nome;
        fornecedorExistente.cnpj = fornecedorAtualizado.cnpj;
        return Response.ok(fornecedorExistente).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletarFornecedor(@PathParam("id") Long id){
        if(repository.deleteById(id)){
            return Response.noContent().build();
        } else {
            throw new NotFoundException();
        }
    }

}
