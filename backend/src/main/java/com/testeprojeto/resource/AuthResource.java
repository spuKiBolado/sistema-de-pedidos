package com.testeprojeto.resource;

import com.testeprojeto.model.Usuario;
import com.testeprojeto.repository.UsuarioRepository;
import com.testeprojeto.resource.dto.RegistroDTO;

import io.quarkus.elytron.security.common.BcryptUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.testeprojeto.model.Fornecedor;
import com.testeprojeto.model.Cliente;
import com.testeprojeto.repository.FornecedorRepository;
import com.testeprojeto.repository.ClienteRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
import jakarta.annotation.security.PermitAll;

@Path("/auth")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
  
    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    FornecedorRepository fornecedorRepository;

    @Inject
    ClienteRepository clienteRepository;

    
    @POST
    @Path("/register")
    @Transactional
    public Response register(RegistroDTO dto) {
        if (usuarioRepository.find("email", dto.email).firstResult() != null) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"E-mail já cadastrado.\"}").build();
        }

        Usuario usuario = new Usuario();
        usuario.email = dto.email;
        usuario.senha = BcryptUtil.bcryptHash(dto.senha);
        usuario.papel = dto.papel;

        if ("FORNECEDOR".equalsIgnoreCase(dto.papel)) {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.nome = dto.nomeEmpresa;
            fornecedor.cnpj = dto.cnpj;
            fornecedorRepository.persist(fornecedor);
            usuario.fornecedor = fornecedor;

        } else if ("CLIENTE".equalsIgnoreCase(dto.papel)) {
            Cliente cliente = new Cliente();
            cliente.nome = dto.nomeEmpresa;
            cliente.cnpj = dto.cnpj;
            clienteRepository.persist(cliente);
            usuario.cliente = cliente;
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Papel inválido.\"}").build();
        }

        usuarioRepository.persist(usuario);
        return Response.status(Response.Status.CREATED).build();
    }

    public static class LoginResponse {
        public String token;
        public String papel;
        public Long profileId;

        public LoginResponse(String token, String papel, Long profileId) {
            this.token = token;
            this.papel = papel;
            this.profileId = profileId;
        }
    }

    @POST
    @Path("/login")
    public Response login(Usuario credentials) {
        Usuario usuario = usuarioRepository.find("email", credentials.email).firstResult();
        if (usuario == null || !BcryptUtil.matches(credentials.senha, usuario.senha)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            // Usando a nova biblioteca para criar o token
            Algorithm algorithm = Algorithm.HMAC256("seu-segredo-super-secreto");
            String token = JWT.create()
                .withIssuer("meu-projeto-issuer")
                .withSubject(usuario.email) // O 'subject' é o identificador do usuário
                .withClaim("role", usuario.papel) // Adicionamos o papel do usuário
                .sign(algorithm);

            // Para o frontend, ainda precisamos do profileId
            Long profileId = "FORNECEDOR".equals(usuario.papel) ? usuario.fornecedor.id : usuario.cliente.id;

            return Response.ok(new LoginResponse(token, usuario.papel, profileId)).build();

        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/ping")
    public String ping() {
        return "pong";
    }
    

}
