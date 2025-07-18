package com.testeprojeto.resource;

import com.testeprojeto.model.Cliente;
import com.testeprojeto.model.Pedido;
import com.testeprojeto.model.Produto;
import com.testeprojeto.repository.ClienteRepository;
import com.testeprojeto.repository.PedidoRepository;
import com.testeprojeto.repository.ProdutoRepository;
import com.testeprojeto.model.enums.StatusPedido;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;


@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidoResource {

    @Inject
    PedidoRepository pedidoRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ProdutoRepository produtoRepository;

    @POST
    @Transactional
    public Response criarPedido(Pedido pedidoRequest) {
        Objects.requireNonNull(pedidoRequest.cliente, "Cliente é obrigatório");
        Objects.requireNonNull(pedidoRequest.cliente.id, "ID do Cliente é obrigatório");
        if (pedidoRequest.itens == null || pedidoRequest.itens.isEmpty()) {
            throw new WebApplicationException("A lista de itens não pode ser vazia", 400);
        }

        Cliente cliente = clienteRepository.findById(pedidoRequest.cliente.id);
        if (cliente == null) {
            throw new NotFoundException("Cliente com id " + pedidoRequest.cliente.id + " não encontrado");
        }

        Pedido novoPedido = new Pedido();
        novoPedido.cliente = cliente;
        novoPedido.dataDoPedido = LocalDate.now();
        novoPedido.status = StatusPedido.PENDENTE;
        novoPedido.itens = new ArrayList<>();
        
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (Produto itemRequest : pedidoRequest.itens) {
            Objects.requireNonNull(itemRequest.id, "ID do Produto é obrigatório para cada item");
            Produto produtoDoBanco = produtoRepository.findById(itemRequest.id);
            if (produtoDoBanco == null) {
                throw new NotFoundException("Produto com id " + itemRequest.id + " não encontrado");
            }
            novoPedido.itens.add(produtoDoBanco);
            if (produtoDoBanco.preco != null) {
                valorTotal = valorTotal.add(produtoDoBanco.preco);
            }
        }

        novoPedido.valorTotal = valorTotal;

        pedidoRepository.persist(novoPedido);

        return Response.status(Response.Status.CREATED).entity(novoPedido).build();
    }

    @GET
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.listAll();
        pedidos.forEach(p -> p.itens.size()); 
        return pedidos;
    }

    @GET
    @Path("/{id}")
    public Pedido buscarPorId(@PathParam("id") Long id) {
        Pedido pedido = pedidoRepository.findById(id);
        if (pedido == null) {
            throw new NotFoundException("Pedido com id " + id + " não encontrado");
        }
        pedido.itens.size(); 
        return pedido;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizarStatus(@PathParam("id") Long id, Map<String, String> requestBody){
        Pedido pedido = buscarPorId(id);

        String novoStatusString = requestBody.get("status");
        if (novoStatusString == null || novoStatusString.isBlank()){
            throw new WebApplicationException("O campo 'status' é obrigatório.", 400);
        }

        try {
            StatusPedido novoStatus = StatusPedido.valueOf(novoStatusString.toUpperCase());
            pedido.status = novoStatus;
        } catch (IllegalArgumentException e){
            throw new WebApplicationException("Status '" + novoStatusString + "' é inválido.", 400);

        }
        return Response.ok(pedido).build();

    }

}