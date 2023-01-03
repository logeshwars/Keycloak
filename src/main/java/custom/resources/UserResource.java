package custom.resources;

import custom.jpa.CustomUserEntity;
import custom.spi.UserRepresentation;
import custom.spi.UserService;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class UserResource {
    private final KeycloakSession session;

    public UserResource(KeycloakSession keycloakSession) {
        this.session = keycloakSession;
    }

    @GET
    @Path("")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserRepresentation> getUsers()
    {
        return session.getProvider(UserService.class).getUsers();
    }

    @POST
    @Path("")
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUsers(UserRepresentation user)
    {
        session.getProvider(UserService.class).addUser(user);
        return Response.created(session.getContext().getUri().getAbsolutePathBuilder().path(user.getId()).build()).build();

    }
}
