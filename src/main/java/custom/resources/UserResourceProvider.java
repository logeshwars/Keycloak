package custom.resources;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class UserResourceProvider implements RealmResourceProvider {
    private final KeycloakSession keycloakSession;

    public UserResourceProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Override
    public Object getResource() {
        return null;
    }

    @Override
    public void close() {

    }
}
