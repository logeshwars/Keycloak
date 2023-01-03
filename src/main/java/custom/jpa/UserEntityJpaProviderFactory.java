package custom.jpa;

import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UserEntityJpaProviderFactory implements JpaEntityProviderFactory {
    public static String FACTORY_ID= "user_entity_jpa_provider";
    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        return new UserEntityJpaProvider();
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return FACTORY_ID;
    }
}
