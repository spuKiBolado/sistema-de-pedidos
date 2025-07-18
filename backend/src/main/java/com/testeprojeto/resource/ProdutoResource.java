package com.testeprojeto.resource;

import com.testeprojeto.model.Fornecedor;
import com.testeprojeto.model.Produto;
import com.testeprojeto.repository.FornecedorRepository; 
import com.testeprojeto.repository.ProdutoRepository; 
import jakarta.inject.Inject; 
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import jakarta.annotation.security.RolesAllowed;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject 
    ProdutoRepository produtoRepository;

    @Inject
    FornecedorRepository fornecedorRepository;

    @POST
    @Transactional
    public Response criarProduto(Produto produto) {

        Objects.requireNonNull(produto.fornecedor, "O fornecedor não pode ser nulo");
        Objects.requireNonNull(produto.fornecedor.id, "O id do fornecedor não pode ser nulo");

        Fornecedor fornecedor = fornecedorRepository.findById(produto.fornecedor.id);
        if (fornecedor == null){
            throw new WebApplicationException("Fornecedor com id " + produto.fornecedor.id + " não existe. ", Response.Status.BAD_REQUEST);   
        }

        produto.fornecedor = fornecedor;
        produtoRepository.persist(produto);

        return Response.created(URI.create("/produtos/" + produto.id)).entity(produto).build();
    }

    @GET
    public List<Produto> listarTodos() {
        return produtoRepository.listAll(); 
    }

    @GET
    @Path("/{id}")
    public Produto buscarPorId(@PathParam("id") Long id) {
        Produto produto = produtoRepository.findById(id);
        if (produto == null) {
            throw new NotFoundException();
        }
        return produto;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizarProduto(@PathParam("id") Long id, Produto produtoAtualizado) {
        Produto produtoExistente = buscarPorId(id);

        produtoExistente.nome = produtoAtualizado.nome;
        produtoExistente.descricao = produtoAtualizado.descricao;
        produtoExistente.preco = produtoAtualizado.preco;

        if (produtoAtualizado.fornecedor != null && produtoAtualizado.fornecedor.id != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(produtoAtualizado.fornecedor.id);
            if (fornecedor == null) {
                throw new WebApplicationException("Fornecedor com id " + produtoAtualizado.fornecedor.id + " não existe.", Response.Status.BAD_REQUEST);
            }
            produtoExistente.fornecedor = fornecedor;
        } else {
             
        }

        return Response.ok(produtoExistente).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletarProduto(@PathParam("id") Long id) {
        boolean deletado = produtoRepository.deleteById(id); 
        if (deletado) {
            return Response.noContent().build();
        } else {
            throw new NotFoundException("Produto com id " + id + " não encontrado.");
        }
    }
}