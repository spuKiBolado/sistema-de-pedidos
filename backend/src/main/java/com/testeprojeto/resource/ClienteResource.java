package com.testeprojeto.resource;

import com.testeprojeto.model.Cliente;
import com.testeprojeto.repository.ClienteRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteRepository repository;

    @POST
    @Transactional
    public Response criarCliente(Cliente cliente) {
        repository.persist(cliente);
        return Response.created(URI.create("/clientes/" + cliente.id)).entity(cliente).build();
    }

    @GET
    public List<Cliente> listarTodos() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    public Cliente buscarPorId(@PathParam("id") Long id) {
        Cliente cliente = repository.findById(id);
        if (cliente == null) {
            throw new NotFoundException();
        }
        return cliente;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizarCliente(@PathParam("id") Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);
        clienteExistente.nome = clienteAtualizado.nome;
        clienteExistente.cnpj = clienteAtualizado.cnpj;
        return Response.ok(clienteExistente).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletarCliente(@PathParam("id") Long id) {
        if (repository.deleteById(id)) {
            return Response.noContent().build();
        } else {
            throw new NotFoundException();
        }
    }
}