package custom.resources;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.Path;


public class UserRestResource {

    private final KeycloakSession keycloakSession;
    private final AuthenticationManager.AuthResult auth;

    public UserRestResource(KeycloakSession keycloakSession, AuthenticationManager.AuthResult auth) {
        this.keycloakSession = keycloakSession;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(keycloakSession).authenticate();
    }

    @Path("customusers")
    public UserResource getUserResource()
    {
        return  new UserResource(keycloakSession);
    }
}
